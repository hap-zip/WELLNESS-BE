package com.example.wellness.connectionview.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "patterns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PatternEntity {
    public enum PatternType {
        ASSOCIATION,
        CONDITION,
        TREND
    }

    public enum PatternStatus {
        INSUFFICIENT,
        POSSIBLE,
        CONFIRMED
    }

    public enum RelationDirection {
        POSITIVE,
        NEGATIVE,
        NO_CLEAR_RELATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pattern_name", nullable = false)
    private String patternName;

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern_type", nullable = false)
    private PatternType patternType;

    @Column(name = "source_metric", nullable = false)
    private String sourceMetric;

    @Column(name = "target_metric", nullable = false)
    private String targetMetric;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_direction", nullable = false)
    private RelationDirection relationDirection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatternStatus status;

    @Column(name = "analysis_start_date", nullable = false)
    private LocalDate analysisStartDate;

    @Column(name = "analysis_end_date", nullable = false)
    private LocalDate analysisEndDate;
}