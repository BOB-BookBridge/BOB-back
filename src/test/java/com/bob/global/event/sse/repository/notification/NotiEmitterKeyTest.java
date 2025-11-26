package com.bob.global.event.sse.repository.notification;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("알림 Emitter key 반환 테스트")
class NotiEmitterKeyTest {

    @Test
    void Emitter_키_반환() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);

        String result = key.toString();

        assertThat(result).isEqualTo("noti:" + MEMBER_ID);
    }
}
