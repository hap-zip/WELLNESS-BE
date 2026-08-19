package com.example.wellness.wellnesschat.repository;

import com.example.wellness.wellnesschat.dto.TriggerType;
import com.example.wellness.wellnesschat.entity.PersistentSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersistentSignalRepository extends JpaRepository<PersistentSignal, Long> {

    List<PersistentSignal> findByUserIdOrderByTriggeredAtDesc(Long userId);

    // 같은 부위·같은 유형에 아직 해소 안 된(resolvedAt == null) 신호가 있는지 확인용 (중복 방지)
    Optional<PersistentSignal> findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(
            Long userId, String painArea, TriggerType triggerType);
}