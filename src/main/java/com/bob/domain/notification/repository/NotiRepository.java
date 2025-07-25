package com.bob.domain.notification.repository;

import com.bob.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface NotiRepository extends CrudRepository<Notification, Long> {

  List<Notification> findByReceiverIdAndCreatedAtAfter(UUID receiverId, LocalDateTime createdAt);
}
