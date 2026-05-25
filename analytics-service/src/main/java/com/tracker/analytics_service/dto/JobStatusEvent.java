package com.tracker.analytics_service.dto;

import com.tracker.analytics_service.model.ApplicationState;
import lombok.Data;

@Data
public class JobStatusEvent {
    private Long applicationId;
    private String companyName;
    private ApplicationState previousState;
    private ApplicationState newState;

    /**
     * FRAMEWORK REFLECTION CONSTRUCTOR
     * 
     * Why it is marked private:
     * Marking this zero-argument constructor as 'private' completely blocks
     * developers from
     * manually instantiating empty or uninitialized data objects anywhere in our
     * custom codebase.
     * 
     * Why it is left blank:
     * The framework serialization layer (Jackson) utilizes low-level reflection
     * protocols. It
     * can bypass standard access controls to instantiate this private block and
     * securely hydrate
     * fields directly from incoming Kafka JSON streams.
     */
    public JobStatusEvent() {
        // Safe, blank constructor accessible ONLY via background system reflection
        // frameworks
    }
}