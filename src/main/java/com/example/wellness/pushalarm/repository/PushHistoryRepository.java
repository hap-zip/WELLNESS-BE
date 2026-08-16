package com.example.wellness.pushalarm.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.pushalarm.domain.PushHistory;
public interface PushHistoryRepository extends JpaRepository<PushHistory, Long> {}

// 그냥 푸시 로그 저장용.