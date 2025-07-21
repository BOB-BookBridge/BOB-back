package com.bob.domain.notification.service;

import static com.bob.domain.notification.entity.NotificationType.CHAT;
import static com.bob.domain.notification.service.dto.response.NotiMemberResponse.from;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.entity.NotificationType;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.domain.notification.service.dto.response.NotiMemberResponse;
import com.bob.domain.notification.service.port.NotiMemberPort;
import com.bob.domain.notification.service.port.NotiRedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotiService {

  private final NotiRepository notiRepository;
  private final NotiMemberPort memberPort;
  private final NotiRedisPort redisPort;

  @Transactional
  public void createNotificationProcess(CreateNotiCommand command) {
    NotiMemberResponse sender = from(memberPort.readNotiMemberProfile(command.senderId()));
    if (!isChatNoti(command.type())) {
      Notification notification = command.toTradeNotiEntity(sender.nickname());
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
}
