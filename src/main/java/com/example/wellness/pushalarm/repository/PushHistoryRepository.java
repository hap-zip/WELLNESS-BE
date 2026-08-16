package com.example.wellness.pushalarm.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.pushalarm.domain.PushHistory;
public interface PushHistoryRepository extends JpaRepository<PushHistory, Long> {}