package com.tracker.job_workflow_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tracker.job_workflow_service.model.ApplicationState;
import com.tracker.job_workflow_service.dto.JobApplicationDTO;
import com.tracker.job_workflow_service.model.JobApplication;
import com.tracker.job_workflow_service.service.JobApplicationService;

import java.util.List;

// '@RestController' tells Spring Boot that this class defines network API endpoints 
// and will automatically return data formatted as universal JSON strings.
// '@RequestMapping("/api/jobs")' prefixes all our URLs below with this base path.
@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {

    private final JobApplicationService service;

    // Manual constructor injection to safely connect to our service layer component
    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    /**
     * Endpoint 1: Create a Job Application Tracker Item
     * URL: POST http://localhost:8080/api/jobs
     * Header Parameter: X-User-Id (simulating an authenticated user ID string)
     */
    @PostMapping
    public ResponseEntity<JobApplication> createJob(
            @RequestBody JobApplicationDTO dto, // CHANGED: Now receives our clean POJO/DTO object
            @RequestHeader("X-User-Id") String userId) {

        // MANUAL TRANSLATION: Create an instance of our database entity
        JobApplication applicationEntity = new JobApplication();

        // Copy the raw network values safely from the DTO over to the target entity
        applicationEntity.setCompanyName(dto.getCompanyName());
        applicationEntity.setRoleTitle(dto.getRoleTitle());
        applicationEntity.setState(dto.getState());
        applicationEntity.setSalaryRange(dto.getSalaryRange());

        // Forward our safely constructed database entity down to our service brain
        // layer
        JobApplication created = service.createApplication(applicationEntity, userId);

        // Return our HTTP 200 OK along with our final database entity response
        return ResponseEntity.ok(created);
    }

    /**
     * Endpoint 2: Fetch the active Kanban Board
     * URL: GET http://localhost:8080/api/jobs
     * Header Parameter: X-User-Id
     */
    @GetMapping
    public ResponseEntity<List<JobApplication>> getBoard(@RequestHeader("X-User-Id") String userId) {
        List<JobApplication> board = service.getKanbanBoard(userId);
        return ResponseEntity.ok(board);
    }

    /**
     * Endpoint 3: Update a Job Status (Move a Kanban Card)
     * URL: PUT http://localhost:8080/api/jobs/{id}/state
     * Query Parameter: ?newState=INTERVIEWING
     * Header Parameter: X-User-Id
     */
    @PutMapping("/{id}/state")
    public ResponseEntity<JobApplication> updateState(
            @PathVariable Long id,
            @RequestParam ApplicationState newState,
            @RequestHeader("X-User-Id") String userId) {

        JobApplication updated = service.updateApplicationState(id, newState, userId);
        return ResponseEntity.ok(updated);
    }
}