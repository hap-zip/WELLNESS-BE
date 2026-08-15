package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.repository;

import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {

    Optional<DailyCheck> findByUserIdAndCheckDate(Long userId, LocalDate checkDate);

    List<DailyCheck> findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    void deleteByUserIdAndCheckDate(Long userId, LocalDate checkDate);
}
