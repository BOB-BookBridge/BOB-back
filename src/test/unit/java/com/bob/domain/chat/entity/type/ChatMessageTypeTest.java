package com.bob.domain.chat.entity.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ChatMessageType 테스트")
class ChatMessageTypeTest {

  @Test
  @DisplayName("hasFile - MESSAGE, SYSTEM 테스트")
  void hasFile은_MESSAGE와_SYSTEM에_대해_false를_반환한다() {
    // when & then
    assertThat(ChatMessageType.MESSAGE.hasFile()).isFalse();
    assertThat(ChatMessageType.SYSTEM.hasFile()).isFalse();
  }

  @Test
  @DisplayName("hasFile - IMAGE, MIX 테스트")
  void hasFile은_IMAGE와_MIX에_대해_true를_반환한다() {
    // when & then
    assertThat(ChatMessageType.IMAGE.hasFile()).isTrue();
    assertThat(ChatMessageType.MIX.hasFile()).isTrue();
  }
}
