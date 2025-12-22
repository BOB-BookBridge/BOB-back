package com.bob.support.fixture.notification.dto.command;

import java.util.List;
import java.util.UUID;

import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.domain.NotificationType;

public class CreateNotificationCommandFixture {

    public static CreateNotificationCommand createCreateNotificationCommand(
        NotificationType type, String refId, UUID senderId, UUID receiverId, boolean isSystem
    ) {
        return CreateNotificationCommand.builder()
            .type(type)
            .refId(refId)
            .childId("1")
            .senderId(senderId)
            .receiverId(receiverId)
            .body("body")
            .fileNames(List.of())
            .isSystem(isSystem)
            .normalize(false)
            .build();
    }

    public static CreateNotificationCommand createCreateNotificationCommand(
        NotificationType type, UUID senderId, UUID receiverId
    ) {
        return createCreateNotificationCommand(type, "1", senderId, receiverId, false);
    }
}
