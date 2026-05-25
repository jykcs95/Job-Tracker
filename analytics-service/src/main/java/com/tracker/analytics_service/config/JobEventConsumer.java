package com.tracker.analytics_service.config;

import com.tracker.analytics_service.dto.JobStatusEvent;
import com.tracker.analytics_service.model.ApplicationState;
import com.tracker.analytics_service.model.DailyStateAggregate;
import com.tracker.analytics_service.repository.DailyStateAggregateRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@Service
public class JobEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(JobEventConsumer.class);

    private final DailyStateAggregateRepository repository;
    private final SseConnectionManager sseManager;

    // Explicit constructor to hook up our analytics database tracking layer
    public JobEventConsumer(DailyStateAggregateRepository repository, SseConnectionManager sseManager) {
        this.repository = repository;
        this.sseManager = sseManager;
    }

    /**
     * '@KafkaListener' tells Spring Boot to keep a persistent open network
     * connection
     * to our Docker Kafka cluster. It monitors the 'job-status-events' topic and
     * automatically converts incoming messages into our custom 'JobStatusEvent' DTO
     * object.
     * '@Transactional' ensures that both database updates succeed together or fail
     * together.
     */
    @KafkaListener(topics = "job-status-events", groupId = "analytics-group-vfinal", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void consumeStatusChangeEvent(
            JobStatusEvent event,
            @Header(name = KafkaUtils.VALUE_DESERIALIZER_EXCEPTION_HEADER, required = false) Throwable exception) {

        // SAFE GATEWAY: If an exception object exists, Kafka caught a corrupted
        // historical message!
        if (exception != null) {
            log.error(
                    "⚠️ [KAFKA TELEMETRY LAYER] Caught a corrupted historical message on the conveyor belt. Skipping poison pill message safely to clear the queue loop. Cause: {}",
                    exception.getMessage(), exception);
            return; // Aborts processing this message immediately and lets Kafka move forward!
        }

        if (event == null) {
            log.warn("Received a null JobStatusEvent from Kafka, skipping processing.");
            return;
        }

        log.info("Received background event from Kafka! Processing Job ID: {} changing states.",
                event.getApplicationId());
        LocalDate today = LocalDate.now();

        // PART 1: Decrement the calculation counter for the OLD state (if there was
        // one)
        if (event.getPreviousState() != null) {
            updateMetricCounter(today, event.getPreviousState(), -1L);
        }

        // PART 2: Increment the calculation counter for the NEW target state
        if (event.getNewState() != null) {
            updateMetricCounter(today, event.getNewState(), 1L);
        }
    }

    /**
     * Core aggregation logic: Finds an existing tracking row or builds a new one.
     */
    private void updateMetricCounter(LocalDate date, ApplicationState state, Long adjustmentAmount) {
        // Search MySQL to check if we already have a record totaling this state for
        // today
        DailyStateAggregate aggregate = repository.findByLogDateAndState(date, state)
                .orElseGet(() -> {
                    // Fallback: If no tracking row exists for today, build a fresh row shell
                    // initialized at zero
                    DailyStateAggregate newRow = new DailyStateAggregate();
                    newRow.setLogDate(date);
                    newRow.setState(state);
                    newRow.setTotalCount(0L);
                    return newRow;
                });

        // Calculate the fresh total value
        long updatedTotal = aggregate.getTotalCount() + adjustmentAmount;

        // Safety guard rail: Metric totals should never drop below zero rows
        aggregate.setTotalCount(Math.max(0L, updatedTotal));

        // Save our updated aggregations row back into MySQL permanently
        repository.save(aggregate);

        log.info("Metrics calculation synchronized. State: {} | Today's Updated Total Count: {}",
                state, aggregate.getTotalCount());

        java.util.List<DailyStateAggregate> freshMetrics = repository.findAll();
        sseManager.broadcastUpdate(freshMetrics);
    }
}
