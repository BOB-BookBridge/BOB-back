package com.bob.core.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.notification.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByReceiverIdAndCreatedAtAfter(UUID receiverId, LocalDateTime createdAt);
}
