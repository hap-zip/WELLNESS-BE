package com.example.wellnesschatbackend.wellnessdailyexpert.health.entity;

import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums.AutoSource;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** 기기에서 동기화된 날짜별 원본 웨어러블 데이터. daily_checks.auto_*는 이 값을 유저가 고쳤을 때만 별도 저장한다. */
@Getter
@Setter
@Entity
@Table(name = "health_data", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "record_date" }))
public class HealthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "sleep_duration_minutes")
    private Integer sleepDurationMinutes;

    @Column(name = "bedtime")
    private LocalTime bedtime;

    @Column
    private Integer steps;

    @Convert(converter = AutoSource.Converter.class)
    @Column(nullable = false, length = 20)
    private AutoSource source;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
