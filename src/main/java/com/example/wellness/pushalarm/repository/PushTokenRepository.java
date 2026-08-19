package com.example.wellness.pushalarm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.pushalarm.domain.PushToken;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    List<PushToken> findByUserIdAndActiveTrue(String userId);
    Optional<PushToken> findByUserIdAndDeviceId(String userId, String deviceId);
    List<PushToken> findByToken(String token);
}