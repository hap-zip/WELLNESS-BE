package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.entity;

import com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.enums.ReportPeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "expert_cards")
public class ExpertCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Convert(converter = ReportPeriod.Converter.class)
    @Column(nullable = false, length = 10)
    private ReportPeriod period;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "include_sleep", nullable = false)
    private boolean includeSleep = true;

    @Column(name = "include_activity", nullable = false)
    private boolean includeActivity = true;

    @Column(name = "include_discomfort", nullable = false)
    private boolean includeDiscomfort = true;

    @Column(name = "include_routines", nullable = false)
    private boolean includeRoutines = true;

    @Column(name = "hide_personal_info", nullable = false)
    private boolean hidePersonalInfo = false;

    @Column(columnDefinition = "TEXT")
    private String headline;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Highlight> highlights;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discomfort_areas", columnDefinition = "json")
    private List<String> discomfortAreas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sleep_postures", columnDefinition = "json")
    private List<String> sleepPostures;

    @Column(name = "routine_count", nullable = false)
    private int routineCount;

    @Column(name = "feedback_summary", columnDefinition = "TEXT")
    private String feedbackSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discovered_patterns", columnDefinition = "json")
    private List<String> discoveredPatterns;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "share_token", unique = true, length = 64)
    private String shareToken;

    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Highlight {
        private String label;
        private String value;
        private String change;
    }
}
