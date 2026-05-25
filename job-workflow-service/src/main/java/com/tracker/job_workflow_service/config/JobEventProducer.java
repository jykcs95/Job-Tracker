package com.tracker.job_workflow_service.config;

import com.tracker.job_workflow_service.dto.JobStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// '@Component' registers this class as a reusable bean inside Spring Boot's memory.
@Component
public class JobEventProducer {

    private static final Logger log = LoggerFactory.getLogger(JobEventProducer.class);

    // The name of the specific message pipeline (topic) inside Kafka we will send
    // data to
    private static final String TOPIC = "job-status-events";

    // KafkaTemplate is a Spring helper class that does the actual networking work
    // to talk to Kafka
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Explicit manual constructor injection to wire our Kafka template safely
    public JobEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a status-change event out to the Apache Kafka cluster broker.
     */
    public void sendStatusChangeEvent(JobStatusEvent event) {
        log.info("Publishing Kafka event to topic '{}' for Job ID: {}. State changed: {} -> {}",
                TOPIC, event.getApplicationId(), event.getPreviousState(), event.getNewState());

        // Pass the topic name, a key (the job ID as text), and the entire event object
        // payload
        this.kafkaTemplate.send(TOPIC, String.valueOf(event.getApplicationId()), event);
    }
}
