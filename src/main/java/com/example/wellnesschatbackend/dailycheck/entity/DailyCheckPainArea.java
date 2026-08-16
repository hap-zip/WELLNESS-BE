package com.example.wellnesschatbackend.dailycheck.entity;

import com.example.wellnesschatbackend.dailycheck.enums.BodyView;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "daily_check_pain_areas", uniqueConstraints = @UniqueConstraint(columnNames = { "daily_check_id",
        "zone_id" }))
public class DailyCheckPainArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_check_id", nullable = false)
    private DailyCheck dailyCheck;

    @Column(name = "zone_id", nullable = false, length = 30)
    private String zoneId;

    @Convert(converter = BodyView.Converter.class)
    @Column(nullable = false, length = 5)
    private BodyView view;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer intensity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
