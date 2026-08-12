package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.repository;

import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, UUID> {

    Optional<DailyCheck> findByUserIdAndCheckDate(UUID userId, LocalDate checkDate);

    List<DailyCheck> findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(UUID userId, LocalDate startDate, LocalDate endDate);

    void deleteByUserIdAndCheckDate(UUID userId, LocalDate checkDate);
}
