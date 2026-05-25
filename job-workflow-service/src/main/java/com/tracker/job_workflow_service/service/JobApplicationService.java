package com.tracker.job_workflow_service.service;

import org.slf4j.Logger; // Added for structured logging
import org.slf4j.LoggerFactory; // Added for initializing the logger instance
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tracker.job_workflow_service.config.JobEventProducer;
import com.tracker.job_workflow_service.dto.JobStatusEvent;
import com.tracker.job_workflow_service.model.ApplicationState;
import com.tracker.job_workflow_service.model.JobApplication;
import com.tracker.job_workflow_service.repository.JobApplicationRepository;

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

    // NEW CONNECTOR: Wire up our custom Kafka event broadcaster component
    private final JobEventProducer eventProducer;

    // Explicit constructor to initialize our database, cache, and message broker
    // links safely
    public JobApplicationService(
            JobApplicationRepository repository,
            RedisTemplate<String, Object> redisTemplate,
            JobEventProducer eventProducer) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.eventProducer = eventProducer;
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

        // Step 3: Publish a Kafka event for new job creations so analytics chart
        // receives an immediate update for the APPLIED state.
        JobStatusEvent event = new JobStatusEvent(
                savedApplication.getId(),
                savedApplication.getCompanyName(),
                null,
                savedApplication.getState());
        eventProducer.sendStatusChangeEvent(event);

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

        // STEP 2: Keep track of what state the job card used to be in before we change
        // it
        ApplicationState oldState = application.getState();

        // STEP 3: Apply the new target state and save it permanently inside MySQL
        application.setState(newState);
        JobApplication updatedApplication = repository.save(application);

        // STEP 4: Clear the outdated Redis cache board key
        redisTemplate.delete(getCacheKey(userId));

        // STEP 5: ASYNCHRONOUS KAFKA TRIGGER!
        // Package the event details and broadcast them over the message bus topic.
        // This notifies our background Analytics Engine instantly without freezing our
        // UI thread.
        JobStatusEvent event = new JobStatusEvent(
                updatedApplication.getId(),
                updatedApplication.getCompanyName(),
                oldState,
                newState);
        eventProducer.sendStatusChangeEvent(event);

        return updatedApplication;
    }

    /**
     * Requirement 4: Delete a job application and notify the message bus.
     */
    public void deleteApplication(Long id, String userId) {
        log.info("Attempting to delete job application ID: {} for user: {}", id, userId);

        // Step 1: Find the application to know its current state before it's gone
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found with ID: " + id));

        ApplicationState oldState = application.getState();

        // Step 2: Delete permanently from MySQL
        repository.deleteById(id);

        // Step 3: Evict the stale Redis cache key
        redisTemplate.delete(getCacheKey(userId));

        // Step 4: Broadcast deletion event to Kafka
        // Setting newState to 'null' signals the Analytics Engine to decrement the
        // count of the oldState
        JobStatusEvent event = new JobStatusEvent(
                id,
                application.getCompanyName(),
                oldState,
                null);
        eventProducer.sendStatusChangeEvent(event);
        log.info("Job application ID: {} deleted successfully.", id);
    }
}