package com.bob.support.fixture.command;

import com.bob.domain.notification.entity.NotificationType;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import java.util.List;
import java.util.UUID;

public class CreateNotiCommandFixture {

  public static CreateNotiCommand DEFAULT_TEXT_CHAT_NOTI(UUID senderId, UUID receiverId) {
    return CreateNotiCommand.builder()
        .type(NotificationType.CHAT)
        .refId("1")
        .childId("1")
        .senderId(senderId)
        .receiverId(receiverId)
        .body("안녕")
        .fileNames(List.of())
        .isSystem(false)
        .normalize(false)
        .build();
  }

  public static CreateNotiCommand DEFAULT_TRADE_NOTI(UUID senderId, UUID receiverId) {
    return CreateNotiCommand.builder()
        .type(NotificationType.TRADE)
        .refId("1")
        .childId("1")
        .senderId(senderId)
        .receiverId(receiverId)
        .body("예약")
        .fileNames(List.of())
        .isSystem(true)
        .normalize(false)
        .build();
  }
}
