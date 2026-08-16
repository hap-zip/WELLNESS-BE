package com.example.wellness.wellnesschat.repository;

import com.example.wellness.wellnesschat.entity.WellnessChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WellnessChatRepository extends JpaRepository<WellnessChat, Long> {
    List<WellnessChat> findByUserIdOrderByCreatedAtDesc(Long userId);
}