package com.example.wellness.health.repository;

import com.example.wellness.health.entity.HealthConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthConnectionRepository extends JpaRepository<HealthConnection, Long> {

    Optional<HealthConnection> findByUserId(Long userId);
}
