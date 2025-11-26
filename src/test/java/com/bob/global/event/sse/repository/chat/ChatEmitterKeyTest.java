package com.bob.global.event.sse.repository.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 Emitter key 반환 테스트")
class ChatEmitterKeyTest {

    @Test
    void Emitter_키_반환() {
        Long chatRoomId = 42L;
        UUID memberId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ChatEmitterKey key = new ChatEmitterKey(chatRoomId, memberId);

        String result = key.toString();

        assertThat(result).isEqualTo("chat:42:123e4567-e89b-12d3-a456-426614174000");
    }
}
