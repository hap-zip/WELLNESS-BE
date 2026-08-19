package com.example.wellness.dailyroutine.repository;

import com.example.wellness.dailyroutine.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    Optional<RoutineEntity> findByTargetArea(String targetArea);
}