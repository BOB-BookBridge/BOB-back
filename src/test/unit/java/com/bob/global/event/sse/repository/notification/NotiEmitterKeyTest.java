package com.bob.global.event.sse.repository.notification;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("알림 Emitter key 반환 테스트")
class NotiEmitterKeyTest {

  @Test
  @DisplayName("문자열 반환 테스트")
  void toString_메서드는_정해진_형식으로_반환한다() {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);

    // when
    String result = key.toString();

    // then
    assertThat(result).isEqualTo("noti:"+MEMBER_ID);
  }
}