package com.bob.core.application.notification;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.application.notification.port.in.NotificationReader;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService implements NotificationReader {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification read(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_NOTIFICATION));
    }

    @Override
    public List<Notification> readByMember(ReadByMemberQuery query) {
        LocalDateTime oneMonth = LocalDateTime.now().minusMonths(1);

        return notificationRepository.findByReceiverIdAndCreatedAtAfterOrderByCreatedAtDesc(query.memberId(), oneMonth);
    }
}
