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

    public JobEventConsumer(DailyStateAggregateRepository repository, SseConnectionManager sseManager) {
        this.repository = repository;
        this.sseManager = sseManager;
    }

    @KafkaListener(topics = "job-status-events", groupId = "analytics-group-vfinal", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void consumeStatusChangeEvent(
            JobStatusEvent event,
            @Header(name = KafkaUtils.VALUE_DESERIALIZER_EXCEPTION_HEADER, required = false) Throwable exception) {

        if (exception != null) {
            log.error(
                    "⚠️ [KAFKA TELEMETRY LAYER] Caught a corrupted historical message on the conveyor belt. Skipping poison pill message safely to clear the queue loop. Cause: {}",
                    exception.getMessage(), exception);
            return;
        }

        if (event == null) {
            log.warn("Received a null JobStatusEvent from Kafka, skipping processing.");
            return;
        }

        log.info("Received background event from Kafka! Processing Job ID: {} changing states.",
                event.getApplicationId());
        LocalDate today = LocalDate.now();

        if (event.getPreviousState() != null) {
            updateMetricCounter(today, event.getPreviousState(), -1L);
        }

        if (event.getNewState() != null) {
            updateMetricCounter(today, event.getNewState(), 1L);
        }
    }

    private void updateMetricCounter(LocalDate date, ApplicationState state, Long adjustmentAmount) {

        DailyStateAggregate aggregate = repository.findByLogDateAndState(date, state)
                .orElseGet(() -> {
                    DailyStateAggregate newRow = new DailyStateAggregate();
                    newRow.setLogDate(date);
                    newRow.setState(state);
                    newRow.setTotalCount(0L);
                    return newRow;
                });

        long updatedTotal = aggregate.getTotalCount() + adjustmentAmount;
        aggregate.setTotalCount(Math.max(0L, updatedTotal));
        repository.save(aggregate);

        log.info("Metrics calculation synchronized. State: {} | Today's Updated Total Count: {}",
                state, aggregate.getTotalCount());

        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<DailyStateAggregate> todays = repository.findByLogDate(today);

        java.util.Map<com.tracker.analytics_service.model.ApplicationState, DailyStateAggregate> map = new java.util.EnumMap<>(
                com.tracker.analytics_service.model.ApplicationState.class);
        for (com.tracker.analytics_service.model.ApplicationState s : com.tracker.analytics_service.model.ApplicationState
                .values()) {
            map.put(s, null);
        }
        for (DailyStateAggregate d : todays) {
            map.put(d.getState(), d);
        }

        java.util.List<DailyStateAggregate> toBroadcast = new java.util.ArrayList<>();
        for (com.tracker.analytics_service.model.ApplicationState s : com.tracker.analytics_service.model.ApplicationState
                .values()) {
            DailyStateAggregate d = map.get(s);
            if (d == null) {
                DailyStateAggregate empty = new DailyStateAggregate();
                empty.setLogDate(today);
                empty.setState(s);
                empty.setTotalCount(0L);
                toBroadcast.add(empty);
            } else {
                toBroadcast.add(d);
            }
        }

        sseManager.broadcastUpdate(toBroadcast);
    }
}
