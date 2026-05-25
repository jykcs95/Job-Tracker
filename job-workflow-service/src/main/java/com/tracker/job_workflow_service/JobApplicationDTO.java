package com.tracker.job_workflow_service;

import lombok.Data;

// '@Data' generates standard getters and setters behind the scenes.
// This is a pure DTO/POJO object. It has NO database mapping annotations.
@Data
public class JobApplicationDTO {
    private String companyName;
    private String roleTitle;
    private ApplicationState state;
    private String salaryRange;
}