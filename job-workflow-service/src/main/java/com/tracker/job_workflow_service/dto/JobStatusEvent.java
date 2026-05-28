package com.tracker.job_workflow_service.dto;
import com.tracker.job_workflow_service.model.ApplicationState;
import lombok.Data;
@Data
public class JobStatusEvent {
    private Long applicationId;
    private String companyName;
    private ApplicationState previousState;
    private ApplicationState newState;
    public JobStatusEvent() {
    }
    public JobStatusEvent(Long applicationId, String companyName, ApplicationState previousState,
            ApplicationState newState) {
        this.applicationId = applicationId;
        this.companyName = companyName;
        this.previousState = previousState;
        this.newState = newState;
    }
}
