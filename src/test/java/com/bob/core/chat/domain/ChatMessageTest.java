package com.bob.core.chat.domain;

import static com.bob.core.chat.domain.type.ChatMessageType.IMAGE;
import static com.bob.core.chat.domain.type.ChatMessageType.MIX;
import static com.bob.core.chat.domain.type.ChatMessageType.SYSTEM;
import static com.bob.core.chat.domain.type.ChatMessageType.TEXT;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 메시지 도메인 테스트")
class ChatMessageTest {

    @Test
    void 채팅_메시지_생성() {
        UUID senderId = UUID.randomUUID();
        String content = "안녕하세요";

        ChatMessage message = ChatMessage.createChatMessage(senderId, content, TEXT);

        assertThat(message.getSenderId()).isEqualTo(senderId);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.getType()).isEqualTo(TEXT);
        assertThat(message.getCreatedAt()).isNotNull();
    }

    @Test
    void 시스템_메시지_생성() {
        UUID senderId = UUID.randomUUID();
        String content = "거래가 시작되었습니다";

        ChatMessage message = ChatMessage.createSystemChatMessage(senderId, content);

        assertThat(message.getSenderId()).isEqualTo(senderId);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.getType()).isEqualTo(SYSTEM);
        assertThat(message.getCreatedAt()).isNotNull();
    }

    @Test
    void 메시지_타입_결정_텍스트만() {
        assertThat(ChatMessage.resolveMessageType(null, "안녕하세요")).isEqualTo(TEXT);
        assertThat(ChatMessage.resolveMessageType(List.of(), "안녕하세요")).isEqualTo(TEXT);
    }

    @Test
    void 메시지_타입_결정_이미지만() {
        assertThat(ChatMessage.resolveMessageType(List.of("image.jpg"), null)).isEqualTo(IMAGE);
        assertThat(ChatMessage.resolveMessageType(List.of("image.jpg"), "")).isEqualTo(IMAGE);
        assertThat(ChatMessage.resolveMessageType(List.of("image.jpg"), "   ")).isEqualTo(IMAGE);
    }

    @Test
    void 메시지_타입_결정_혼합() {
        assertThat(ChatMessage.resolveMessageType(List.of("image.jpg"), "안녕하세요")).isEqualTo(MIX);
    }
}
