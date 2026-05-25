package com.tracker.job_workflow_service.dto;

import com.tracker.job_workflow_service.model.ApplicationState;
import lombok.Data;

@Data // Generates getters, setters, and toString fields automatically via Lombok
public class JobStatusEvent {
    private Long applicationId;
    private String companyName;
    private ApplicationState previousState;
    private ApplicationState newState;

    // Default constructor required for JSON serialization libraries
    public JobStatusEvent() {
    }

    // Convenience constructor to build our event objects quickly in our code
    public JobStatusEvent(Long applicationId, String companyName, ApplicationState previousState,
            ApplicationState newState) {
        this.applicationId = applicationId;
        this.companyName = companyName;
        this.previousState = previousState;
        this.newState = newState;
    }
}
