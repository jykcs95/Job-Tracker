package com.tracker.job_workflow_service;

import org.slf4j.Logger; // Added for structured logging
import org.slf4j.LoggerFactory; // Added for initializing the logger instance
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

// '@Service' tells Spring Boot that this class handles the core business logic 
// and should be safely managed inside Spring's memory ecosystem.
@Service
public class JobApplicationService {

    // INITIALIZE LOGGER: Creates a dedicated logging instance for this specific
    // class.
    // It automatically formats your logs with dates, timestamps, thread IDs, and
    // log levels (INFO, WARN, ERROR).
    private static final Logger log = LoggerFactory.getLogger(JobApplicationService.class);

    // Making these 'final' ensures they are initialized immediately and cannot be
    // altered.
    private final JobApplicationRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    // MANUAL CONSTRUCTOR: This explicitly initializes your fields
    // and satisfies Java's initialization rules perfectly without relying on
    // Lombok.
    public JobApplicationService(JobApplicationRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    // Helper method to create a distinct cache key string for Redis
    private String getCacheKey(String userId) {
        return "user:" + userId + ":kanban";
    }

    /**
     * Requirement 1: Create a brand new job application tracker item.
     */
    public JobApplication createApplication(JobApplication application, String userId) {
        // Step 1: Save the new application into our permanent MySQL database
        JobApplication savedApplication = repository.save(application);

        // Step 2: Clear the user's out-of-date Redis cache board
        redisTemplate.delete(getCacheKey(userId));

        return savedApplication;
    }

    /**
     * Requirement 2: Fetch the active Kanban Board (Checks Cache First!)
     */
    public List<JobApplication> getKanbanBoard(String userId) {
        String cacheKey = getCacheKey(userId);

        // Step 1: Check if the user's board is already sitting inside our fast Redis
        // cache
        @SuppressWarnings("unchecked")
        List<JobApplication> cachedBoard = (List<JobApplication>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedBoard != null) {
            log.info("Redis cache hit for user: {}. Fetching Kanban Board directly from memory.", userId);
            return cachedBoard;
        }

        // Step 2: Cache Miss! Read the true data state from our MySQL database
        log.warn("Redis cache miss for user: {}. Querying permanent MySQL database records...", userId);
        List<JobApplication> databaseBoard = repository.findAll();

        // Step 3: Save a copy of this database board back into Redis for next time (10
        // min TTL).
        redisTemplate.opsForValue().set(cacheKey, databaseBoard, 10, TimeUnit.MINUTES);

        return databaseBoard;
    }

    /**
     * Requirement 3: Move a job card to a new state (APPLIED -> INTERVIEWING ->
     * DONE)
     */
    public JobApplication updateApplicationState(Long id, ApplicationState newState, String userId) {
        // Step 1: Find the existing job application inside MySQL, or throw an error
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found with ID: " + id));

        // Step 2: Modify the status field to our new target state
        application.setState(newState);

        // Step 3: Save the edited record back down into MySQL
        JobApplication updatedApplication = repository.save(application);

        // Step 4: Clear the outdated Redis cache key
        redisTemplate.delete(getCacheKey(userId));

        return updatedApplication;
    }
}