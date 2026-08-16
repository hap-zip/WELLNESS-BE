package com.example.wellnesschatbackend.dailyroutine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "routines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_area", nullable = false)
    private String targetArea;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_duration_minutes", nullable = false)
    private Integer totalDurationMinutes;

    @Column(columnDefinition = "TEXT")
    private String precautions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "steps_data", columnDefinition = "json", nullable = false)
    private List<Map<String, Object>> stepsData;
}