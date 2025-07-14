package com.bob.domain.chat.entity;

import static com.bob.support.fixture.domain.chat.ChatRoomMemberFixture.CHAT_ROOM_MEMBER_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ChatRoomMember 도메인 테스트")
class ChatRoomMemberTest {

  @Test
  @DisplayName("나간 시각 업데이트 테스트")
  void exitedAt_시간을_정상적으로_업데이트할_수_있다() {
    // given
    ChatRoomMember member = CHAT_ROOM_MEMBER_1();
    LocalDateTime currentTime = LocalDateTime.now();

    // when
    member.updateExitedAt(currentTime);

    // then
    assertThat(member.getExitedAt()).isEqualTo(currentTime);
  }

  @Test
  @DisplayName("채팅방 재입장 테스트")
  void 채팅방에_재입장하면_입장_시각이_갱신되고_나간_시각은_null이_된다() {
    // given
    ChatRoomMember member = CHAT_ROOM_MEMBER_1();
    LocalDateTime exitedTime = LocalDateTime.now().minusMinutes(5);
    member.updateExitedAt(exitedTime);
    LocalDateTime reEnterTime = LocalDateTime.now();

    // when
    member.reEnterChatRoom(reEnterTime);

    // then
    assertThat(member.getEnteredAt()).isEqualTo(reEnterTime);
    assertThat(member.getExitedAt()).isNull();
  }
}