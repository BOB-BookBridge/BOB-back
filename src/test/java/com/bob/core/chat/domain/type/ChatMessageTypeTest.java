package com.bob.core.chat.domain.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 메시지 타입 테스트")
class ChatMessageTypeTest {

    @Test
    void hasFile_MESSAGE_SYSTEM_false_반환() {
        assertThat(ChatMessageType.TEXT.hasFile()).isFalse();
        assertThat(ChatMessageType.SYSTEM.hasFile()).isFalse();
    }

    @Test
    void hasFile_IMAGE_MIX_false_반환() {
        assertThat(ChatMessageType.IMAGE.hasFile()).isTrue();
        assertThat(ChatMessageType.MIX.hasFile()).isTrue();
    }
}
