package com.tracker.analytics_service.model; // Note: package name matches this service folder

import com.fasterxml.jackson.annotation.JsonFormat;

// THE MAGIC LOOKUP ANNOTATION: Automatically maps 'applied' or 'APPLIED' flawlessly!
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
public enum ApplicationState {
    APPLIED,
    INTERVIEWING,
    DONE
}