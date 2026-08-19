package com.example.wellness.pushalarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wellness.pushalarm.domain.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
}