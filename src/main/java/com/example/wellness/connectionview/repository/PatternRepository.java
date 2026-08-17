package com.example.wellness.connectionview.repository;

import com.example.wellness.connectionview.entity.PatternEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatternRepository extends JpaRepository<PatternEntity, Long> {
    List<PatternEntity> findAllByUserId(Long userId);
}
