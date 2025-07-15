package com.bob.domain.notification.service;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.domain.notification.service.port.NotiMemberPort;
import com.bob.domain.notification.service.port.NotiRedisPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 서비스 테스트")
class NotiServiceTest {

  @InjectMocks
  private NotiService notiService;

  @Mock
  private NotiRepository notiRepository;

  @Mock
  private NotiMemberPort memberPort;

  @Mock
  private NotiRedisPort redisPort;

  @Test
  @DisplayName("CHAT 알림 - 저장 없이 Redis publish만 수행")
  void createNotificationProcess_CHAT이면_저장없이_redis발행() {
    // given
    CreateNotiCommand command = CreateNotiCommand.of("CHAT", "1", "1", MEMBER_ID, OTHER_MEMBER_ID, "메시지", null, false);
    given(memberPort.readNotiMemberProfile(MEMBER_ID)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    notiService.createNotificationProcess(command);

    // then
    verify(notiRepository, never()).save(any());
    verify(redisPort).publish(
        eq(OTHER_MEMBER_ID),
        eq("CHAT"),
        eq("1"),
        eq("1"),
        eq("메시지"),
        eq(null),
        eq(false),
        eq(MEMBER_ID),
        eq("tester"),
        eq("http://image.url")
    );
  }

  @Test
  @DisplayName("TRADE 알림 - 저장 후 Redis publish가 수행된다")
  void createNotificationProcess_TRADE이면_DB저장_및_redis발행() {
    // given
    CreateNotiCommand command = CreateNotiCommand.of("TRADE", "1", "1", MEMBER_ID, OTHER_MEMBER_ID, "거래 완료", null, false);
    given(memberPort.readNotiMemberProfile(MEMBER_ID)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    notiService.createNotificationProcess(command);

    // then
    verify(notiRepository).save(any(Notification.class));
    verify(redisPort).publish(
        eq(OTHER_MEMBER_ID),
        eq("TRADE"),
        eq("1"),
        eq("1"),
        eq("거래 완료"),
        eq(null),
        eq(false),
        eq(MEMBER_ID),
        eq("tester"),
        eq("http://image.url")
    );
  }
}
