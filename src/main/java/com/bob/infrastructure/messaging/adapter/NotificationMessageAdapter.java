package com.bob.infrastructure.messaging.adapter;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.notification.port.out.NotificationMessagePort;
import com.bob.infrastructure.messaging.publisher.RedisPublisher;
import com.bob.infrastructure.messaging.record.RedisRecord;

@Component
@RequiredArgsConstructor
public class NotificationMessageAdapter implements NotificationMessagePort {

    private final RedisPublisher publisher;

    @Override
    public void publish(
        UUID receiverId, String type, String refId, String childId, String body, List<String> fileNames,
        boolean isSystem, boolean normalize, UUID senderId, String senderNickname, String senderProfileImageUrl
    ) {
        publisher.publish(
            RedisRecord.of(
                receiverId, type, refId, childId, body, fileNames,
                isSystem, normalize,
                senderId, senderNickname, senderProfileImageUrl)
        );
    }
}
