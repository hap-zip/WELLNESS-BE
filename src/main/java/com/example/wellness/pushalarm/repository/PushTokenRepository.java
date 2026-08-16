package com.example.wellness.pushalarm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.pushalarm.domain.PushToken;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    
    // 사용자의 활성된 코드 찾기
    List<PushToken> findByUserIdAndActiveTrue(String userId);

    // 사용자 기기나 이름으로 연결된 토큰 유무 확인
    Optional<PushToken> findByUserIdAndDeviceId(String userId, String deviceId);

    // 똑같은 토큰 값 유무 확인 (혹시 다른사람과 겹칠수도있으니)
    List<PushToken> findByToken(String token);
}