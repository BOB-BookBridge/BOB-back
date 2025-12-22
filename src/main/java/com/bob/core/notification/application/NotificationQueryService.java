package com.bob.core.notification.application;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.notification.application.dto.query.ReadByMemberQuery;
import com.bob.core.notification.application.port.in.NotificationReader;
import com.bob.core.notification.domain.Notification;
import com.bob.core.notification.domain.repository.NotificationRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService implements NotificationReader {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification read(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. id : " + id));
    }

    @Override
    public List<Notification> readByMember(ReadByMemberQuery query) {
        LocalDateTime oneMonth = LocalDateTime.now().minusMonths(1);

        return notificationRepository.findByReceiverIdAndCreatedAtAfterOrderByCreatedAtDesc(query.memberId(), oneMonth);
    }
}
