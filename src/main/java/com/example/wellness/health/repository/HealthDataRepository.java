package com.example.wellness.health.repository;

import com.example.wellness.health.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

    Optional<HealthData> findByUserIdAndRecordDate(Long userId, LocalDate recordDate);
}
