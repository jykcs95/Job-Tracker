package com.tracker.job_workflow_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
public enum ApplicationState {
    APPLIED,
    INTERVIEWING,
    DONE
}
