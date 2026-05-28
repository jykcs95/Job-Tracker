package com.tracker.job_workflow_service.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.tracker.job_workflow_service.config.JobEventProducer;
import com.tracker.job_workflow_service.dto.JobStatusEvent;
import com.tracker.job_workflow_service.model.ApplicationState;
import com.tracker.job_workflow_service.model.JobApplication;
import com.tracker.job_workflow_service.repository.JobApplicationRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Service
public class JobApplicationService {
    private static final Logger log = LoggerFactory.getLogger(JobApplicationService.class);
    private final JobApplicationRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JobEventProducer eventProducer;
    public JobApplicationService(
            JobApplicationRepository repository,
            RedisTemplate<String, Object> redisTemplate,
            JobEventProducer eventProducer) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.eventProducer = eventProducer;
    }
    private String getCacheKey(String userId) {
        return "user:" + userId + ":kanban";
    }
    public JobApplication createApplication(JobApplication application, String userId) {
        JobApplication savedApplication = repository.save(application);
        redisTemplate.delete(getCacheKey(userId));
        JobStatusEvent event = new JobStatusEvent(
                savedApplication.getId(),
                savedApplication.getCompanyName(),
                null,
                savedApplication.getState());
        eventProducer.sendStatusChangeEvent(event);
        return savedApplication;
    }
    public List<JobApplication> getKanbanBoard(String userId) {
        String cacheKey = getCacheKey(userId);
        @SuppressWarnings("unchecked")
        List<JobApplication> cachedBoard = (List<JobApplication>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedBoard != null) {
            log.info("Redis cache hit for user: {}. Fetching Kanban Board directly from memory.", userId);
            return cachedBoard;
        }
        log.warn("Redis cache miss for user: {}. Querying permanent MySQL database records...", userId);
        List<JobApplication> databaseBoard = repository.findAll();
        redisTemplate.opsForValue().set(cacheKey, databaseBoard, 10, TimeUnit.MINUTES);
        return databaseBoard;
    }
    public JobApplication updateApplicationState(Long id, ApplicationState newState, String userId) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found with ID: " + id));
        ApplicationState oldState = application.getState();
        application.setState(newState);
        JobApplication updatedApplication = repository.save(application);
        redisTemplate.delete(getCacheKey(userId));
        JobStatusEvent event = new JobStatusEvent(
                updatedApplication.getId(),
                updatedApplication.getCompanyName(),
                oldState,
                newState);
        eventProducer.sendStatusChangeEvent(event);
        return updatedApplication;
    }
    public void deleteApplication(Long id, String userId) {
        log.info("Attempting to delete job application ID: {} for user: {}", id, userId);
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found with ID: " + id));
        ApplicationState oldState = application.getState();
        repository.deleteById(id);
        redisTemplate.delete(getCacheKey(userId));
        JobStatusEvent event = new JobStatusEvent(
                id,
                application.getCompanyName(),
                oldState,
                null);
        eventProducer.sendStatusChangeEvent(event);
        log.info("Job application ID: {} deleted successfully.", id);
    }
}