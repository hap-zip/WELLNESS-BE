package com.example.wellness.feedback.repository;

import com.example.wellness.feedback.entity.RoutineFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoutineFeedbackRepository extends JpaRepository<RoutineFeedbackEntity, Long> {
    List<RoutineFeedbackEntity> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}