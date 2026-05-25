package com.tracker.analytics_service.repository;

import com.tracker.analytics_service.model.ApplicationState;
import com.tracker.analytics_service.model.DailyStateAggregate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

// '@Repository' registers this interface as a data-access component in Spring's memory ecosystem.
// We extend JpaRepository, pointing to our reporting entity and using 'Long' as its primary key ID data type.
@Repository
public interface DailyStateAggregateRepository extends JpaRepository<DailyStateAggregate, Long> {

    /**
     * CUSTOM QUERY METHOD
     * 
     * How it works:
     * By using Spring Data JPA's special naming convention rules, Spring reads this
     * method name
     * and automatically generates the exact SQL command behind the scenes:
     * 'SELECT * FROM daily_state_aggregates WHERE log_date = ? AND state = ?'
     * 
     * Why it uses Optional:
     * It wraps the response in an 'Optional' container. If a row for that specific
     * day and state
     * exists, it returns the object data. If it doesn't find a row, it returns an
     * empty Optional shell
     * without throwing a dangerous null-pointer crash.
     */
    Optional<DailyStateAggregate> findByLogDateAndState(LocalDate logDate, ApplicationState state);

    // Find all aggregate rows for a specific date
    List<DailyStateAggregate> findByLogDate(LocalDate logDate);
}
