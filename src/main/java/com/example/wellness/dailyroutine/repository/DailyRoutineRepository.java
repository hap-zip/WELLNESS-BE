package com.example.wellness.dailyroutine.repository;

import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRoutineRepository extends JpaRepository<DailyRoutineEntity, Long> {
    Optional<DailyRoutineEntity> findByUserIdAndTargetDate(Long userId, LocalDate targetDate);

    List<DailyRoutineEntity> findAllByUserIdAndIsCompletedTrueAndTargetDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);

    List<DailyRoutineEntity> findAllByUserIdAndIsCompletedTrueAndDelayedFeedbackIdIsNull(Long userId);
}