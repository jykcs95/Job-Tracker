package com.tracker.job_workflow_service.dto;

import com.tracker.job_workflow_service.model.ApplicationState;
import lombok.Data;
import java.time.LocalDate;

@Data
public class JobApplicationDTO {
    private String companyName;
    private String roleTitle;
    private ApplicationState state;
    private String salaryRange;
    private LocalDate appliedDate;
    private String jobUrl;
}