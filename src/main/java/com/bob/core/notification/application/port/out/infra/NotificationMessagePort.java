package com.bob.core.notification.application.port.out.infra;

import java.util.List;
import java.util.UUID;

public interface NotificationMessagePort {

    void publish(
        UUID receiverId, String type, String refId, String childId, String body,
        List<String> fileNames, boolean isSystem, boolean normalize,
        UUID senderId, String senderNickname, String senderProfileImageUrl
    );
}
