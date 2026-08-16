package com.example.wellnesschatbackend.dailycheck.entity;

import com.example.wellnesschatbackend.dailycheck.enums.AutoSource;
import com.example.wellnesschatbackend.dailycheck.enums.Condition;
import com.example.wellnesschatbackend.dailycheck.enums.SleepPosture;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "daily_checks", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "check_date" }))
public class DailyCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    // MySQL 예약어(CONDITION)라서 컬럼명은 condition_status로 분리함.
    @Convert(converter = Condition.Converter.class)
    @Column(name = "condition_status", length = 10)
    private Condition condition;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "condition_tags", columnDefinition = "json")
    private List<String> conditionTags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discomfort_feelings", columnDefinition = "json")
    private List<String> discomfortFeelings;

    @Min(1)
    @Max(5)
    @Column(name = "sleep_satisfaction")
    private Integer sleepSatisfaction;

    @Convert(converter = SleepPosture.Converter.class)
    @Column(name = "sleep_posture", length = 20)
    private SleepPosture sleepPosture;

    @Column(name = "sleep_pillow", length = 30)
    private String sleepPillow;

    @Column(name = "activity_level", length = 30)
    private String activityLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skin_states", columnDefinition = "json")
    private List<String> skinStates;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "auto_sleep_duration_minutes")
    private Integer autoSleepDurationMinutes;

    @Column(name = "auto_bedtime")
    private LocalTime autoBedtime;

    @Column(name = "auto_steps")
    private Integer autoSteps;

    @Convert(converter = AutoSource.Converter.class)
    @Column(name = "auto_source", length = 20)
    private AutoSource autoSource;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skipped_steps", columnDefinition = "json")
    private List<String> skippedSteps;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    @OrderBy("zoneId ASC")
    @OneToMany(mappedBy = "dailyCheck", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DailyCheckPainArea> painAreas = new ArrayList<>();

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

    /**
     * orphanRemoval=true는 컬렉션 참조 교체가 아니라 같은 인스턴스 안에서의 항목 제거만 감지.
     * painAreas = newAreas로 바꾸면 삭제된 부위가 DB에 안 지워지므로 clear 후 재구성.
     */
    public void replacePainAreas(List<DailyCheckPainArea> newAreas) {
        painAreas.clear();
        if (newAreas != null) {
            newAreas.forEach(area -> {
                area.setDailyCheck(this);
                painAreas.add(area);
            });
        }
    }

    public Integer maxPainIntensity() {
        return painAreas.stream().map(DailyCheckPainArea::getIntensity).max(Integer::compareTo).orElse(null);
    }
}
