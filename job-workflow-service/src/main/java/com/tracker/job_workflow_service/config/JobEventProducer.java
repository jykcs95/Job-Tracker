package com.tracker.job_workflow_service.config;
import com.tracker.job_workflow_service.dto.JobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class JobEventProducer {
    private static final Logger log = LoggerFactory.getLogger(JobEventProducer.class);
    private static final String TOPIC = "job-status-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public JobEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendStatusChangeEvent(JobStatusEvent event) {
        log.info("Publishing Kafka event to topic '{}' for Job ID: {}. State changed: {} -> {}",
                TOPIC, event.getApplicationId(), event.getPreviousState(), event.getNewState());
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.getApplicationId()), event);
    }
}
