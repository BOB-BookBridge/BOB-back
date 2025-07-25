package com.bob.domain.notification.service;

import static com.bob.domain.notification.entity.NotificationType.CHAT;
import static com.bob.domain.notification.service.dto.response.NotiMemberResponse.from;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.entity.NotificationType;
import com.bob.domain.notification.reader.NotiReader;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.domain.notification.service.dto.command.ChangeNotificationReadStatusCommand;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.service.dto.response.NotiMemberResponse;
import com.bob.domain.notification.service.dto.response.NotificationsResponse;
import com.bob.domain.notification.service.port.NotiMemberPort;
import com.bob.domain.notification.service.port.NotiRedisPort;
import com.bob.domain.notification.usecase.NotiModifyUseCase;
import com.bob.domain.notification.usecase.NotiReadUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotiService implements NotiReadUseCase, NotiModifyUseCase {

  private final NotiRepository notiRepository;
  private final NotiReader notiReader;

  private final NotiMemberPort memberPort;
  private final NotiRedisPort redisPort;

  @Transactional
  public void createNotificationProcess(CreateNotiCommand command) {
    NotiMemberResponse sender = from(memberPort.readNotiMemberProfile(command.senderId()));
    if (!isChatNoti(command.type())) {
      Notification notification = command.toTradeNotiEntity();
      notiRepository.save(notification);
    }
    redisPublish(command, sender);
  }

  private boolean isChatNoti(NotificationType type) {
    return type == CHAT;
  }

  private void redisPublish(CreateNotiCommand command, NotiMemberResponse sender) {
    redisPort.publish(
        command.receiverId(), command.type().name(), command.refId(), command.childId(), command.body(), command.fileNames(),
        command.isSystem(), command.normalize(), sender.memberId(), sender.nickname(), sender.profile()
    );
  }

  @Transactional(readOnly = true)
  public NotificationsResponse readNotificationsProcess(ReadNotificationsQuery query) {
    return NotificationsResponse.from(notiReader.readNotifications(query.memberId()));
  }

  @Transactional
  public void changeNotificationReadStatusProcess(ChangeNotificationReadStatusCommand command) {
    Notification notification = notiReader.readNotificationById(command.notificationId());
    verifyNotificationOwner(command.memberId(), notification.getReceiverId());
    notification.updateReadStatus(true);
  }

  private void verifyNotificationOwner(UUID requestId, UUID ownerId) {
    if (!ownerId.equals(requestId)) {
      throw new ApplicationException(ApplicationError.NOTIFICATION_ACCESS_DENIED);
    }
  }
}
