package com.tracker.job_workflow_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tracker.job_workflow_service.model.ApplicationState;
import com.tracker.job_workflow_service.dto.JobApplicationDTO;
import com.tracker.job_workflow_service.model.JobApplication;
import com.tracker.job_workflow_service.service.JobApplicationService;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {
    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JobApplication> createJob(
            @RequestBody JobApplicationDTO dto,
            @RequestHeader("X-User-Id") String userId) {
        JobApplication applicationEntity = new JobApplication();
        applicationEntity.setCompanyName(dto.getCompanyName());
        applicationEntity.setRoleTitle(dto.getRoleTitle());
        applicationEntity.setState(dto.getState());
        applicationEntity.setSalaryRange(dto.getSalaryRange());
        applicationEntity.setAppliedDate(dto.getAppliedDate());
        applicationEntity.setJobUrl(dto.getJobUrl());
        JobApplication created = service.createApplication(applicationEntity, userId);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getBoard(@RequestHeader("X-User-Id") String userId) {
        List<JobApplication> board = service.getKanbanBoard(userId);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<JobApplication> updateState(
            @PathVariable Long id,
            @RequestParam ApplicationState newState,
            @RequestHeader("X-User-Id") String userId) {
        JobApplication updated = service.updateApplicationState(id, newState, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        service.deleteApplication(id, userId);
        return ResponseEntity.noContent().build();
    }
}