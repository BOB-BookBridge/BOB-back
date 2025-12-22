package com.bob.core.notification.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.notification.domain.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByReceiverIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID receiverId, LocalDateTime createdAt);

    @Modifying
    @Query("""
        UPDATE Notification n SET n.isRead = true
            WHERE n.receiverId = :receiverId AND n.isRead = false
        """
    )
    void markAllAsReadByReceiverId(UUID receiverId);
}
