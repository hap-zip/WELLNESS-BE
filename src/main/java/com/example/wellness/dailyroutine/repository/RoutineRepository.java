package com.example.wellness.dailyroutine.repository;

import com.example.wellness.dailyroutine.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
}