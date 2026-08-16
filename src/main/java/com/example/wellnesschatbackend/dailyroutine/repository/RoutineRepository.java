package com.example.wellnesschatbackend.dailyroutine.repository;

import com.example.wellnesschatbackend.dailyroutine.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
}