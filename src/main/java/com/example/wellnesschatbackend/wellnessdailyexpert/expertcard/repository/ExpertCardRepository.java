package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.repository;

import com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.entity.ExpertCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertCardRepository extends JpaRepository<ExpertCard, UUID> {

    List<ExpertCard> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId);

    Optional<ExpertCard> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);
}
