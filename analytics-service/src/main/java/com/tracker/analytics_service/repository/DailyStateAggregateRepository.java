package com.tracker.analytics_service.repository;

import com.tracker.analytics_service.model.ApplicationState;
import com.tracker.analytics_service.model.DailyStateAggregate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DailyStateAggregateRepository extends JpaRepository<DailyStateAggregate, Long> {

    Optional<DailyStateAggregate> findByLogDateAndState(LocalDate logDate, ApplicationState state);

    List<DailyStateAggregate> findByLogDate(LocalDate logDate);
}
