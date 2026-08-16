package com.example.wellnesschatbackend.health.entity;

import com.example.wellnesschatbackend.health.enums.HealthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 유저당 연결 1개(FE HealthConnectionSettings와 1:1). */
@Getter
@Setter
@Entity
@Table(name = "health_connections")
public class HealthConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Convert(converter = HealthProvider.Converter.class)
    @Column(nullable = false, length = 20)
    private HealthProvider provider;

    @Column(nullable = false)
    private boolean connected = false;

    @Column(name = "sleep_permission", nullable = false)
    private boolean sleepPermission = false;

    @Column(name = "steps_permission", nullable = false)
    private boolean stepsPermission = false;

    @Column(name = "heart_rate_permission", nullable = false)
    private boolean heartRatePermission = false;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
