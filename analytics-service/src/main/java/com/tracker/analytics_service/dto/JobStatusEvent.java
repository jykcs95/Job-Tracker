package com.tracker.analytics_service.dto;

import com.tracker.analytics_service.model.ApplicationState;
import lombok.Data;

@Data
public class JobStatusEvent {
    private Long applicationId;
    private String companyName;
    private ApplicationState previousState;
    private ApplicationState newState;

    public JobStatusEvent() {
    }
}