package com.bob.support.fixture.chat.domain;

import static com.bob.core.domain.chat.type.ChatMessageType.TEXT;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.domain.chat.ChatMessage;
import com.bob.core.domain.chat.Chatroom;

public class ChatMessageFixture {

    public static ChatMessage addMessage(Chatroom room, Long id, UUID senderId) {
        ChatMessage message = room.addMessage(senderId, "메시지", TEXT);

        ReflectionTestUtils.setField(message, "id", id);

        return message;
    }
}
