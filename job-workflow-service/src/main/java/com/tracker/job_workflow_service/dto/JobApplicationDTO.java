package com.tracker.job_workflow_service.dto;
import com.tracker.job_workflow_service.model.ApplicationState;
import lombok.Data;
@Data
public class JobApplicationDTO {
    private String companyName;
    private String roleTitle;
    private ApplicationState state;
    private String salaryRange;
    private String jobUrl;
}