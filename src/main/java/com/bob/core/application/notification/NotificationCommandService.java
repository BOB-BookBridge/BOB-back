package com.bob.core.application.notification;

import static com.bob.core.domain.notification.Notification.createNotification;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.notification.dto.command.CreateNotificationCommand;
import com.bob.core.application.notification.dto.command.MarkAsReadCommand;
import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.application.notification.port.in.NotificationCreator;
import com.bob.core.application.notification.port.in.NotificationMarker;
import com.bob.core.application.notification.port.in.NotificationReader;
import com.bob.core.application.notification.port.out.NotificationMemberPort;
import com.bob.core.application.notification.port.out.NotificationMessagePort;
import com.bob.core.application.notification.port.result.NotificationMember;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCreator, NotificationMarker {

    private final NotificationRepository notificationRepository;
    private final NotificationReader notificationReader;

    private final NotificationMemberPort memberPort;
    private final NotificationMessagePort messagePort;

    @Override
    public Notification create(CreateNotificationCommand command) {
        NotificationMember sender = memberPort.read(command.senderId());

        Notification notification = createNotification(command.type(), command.refId(), sender.id(), command.body());
        if (!notification.isChatNotification())
            notificationRepository.save(notification);

        publish(command, sender);

        return notification;
    }

    @Override
    public Notification markAsRead(Long notificationId, MarkAsReadCommand command) {
        Notification notification = notificationReader.read(notificationId);

        verifyOwner(command.memberId(), notification.getReceiverId());

        notification.markAsRead();

        return notification;
    }

    @Override
    public List<Notification> markAllAsRead(MarkAsReadCommand command) {
        notificationRepository.markAllAsReadByReceiverId(command.memberId());

        return notificationReader.readByMember(new ReadByMemberQuery(command.memberId()));
    }

    private void verifyOwner(UUID requesterId, UUID ownerId) {
        if (!ownerId.equals(requesterId))
            throw new ApplicationException(ApplicationError.NOTIFICATION_ACCESS_DENIED);
    }

    private void publish(CreateNotificationCommand command, NotificationMember sender) {
        messagePort.publish(
            command.receiverId(), command.type().name(), command.refId(), command.childId(),
            command.body(), command.fileNames(), command.isSystem(), command.normalize(),
            sender.id(), sender.nickname(), sender.profileImageUrl()
        );
    }
}
