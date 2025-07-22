package com.bob.domain.noti.service;

import static com.bob.domain.notification.entity.NotificationType.TRADE;
import static com.bob.support.fixture.command.CreateNotiCommandFixture.DEFAULT_TEXT_CHAT_NOTI;
import static com.bob.support.fixture.command.CreateNotiCommandFixture.DEFAULT_TRADE_NOTI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.domain.notification.service.NotiService;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.infra.redis.subscriber.RedisSubscriber;
import com.bob.support.TestContainerSupport;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("NotiService + RedisPublisher 통합 테스트")
@Transactional
@SpringBootTest
class NotiServiceIntgTest extends TestContainerSupport {

  @Autowired
  private NotiService notiService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private NotiRepository notiRepository;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @MockitoBean
  private RedisSubscriber redisSubscriber;

  private static final UUID RECEIVER_ID = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
  private static final UUID SENDER_ID = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");

  @Test
  @DisplayName("CHAT 알림 - DB 저장 없이 RedisSubscriber가 메시지 처리")
  void CHAT_알림은_DB저장_없이_Subscriber가_메시지를_받는다() {
    // given
    Member sender = memberRepository.findById(SENDER_ID).orElseThrow();
    Member receiver = memberRepository.findById(RECEIVER_ID).orElseThrow();
    CreateNotiCommand command = DEFAULT_TEXT_CHAT_NOTI(sender.getId(), receiver.getId());

    // when
    notiService.createNotificationProcess(command);

    // then
    assertThat(notiRepository.findAll()).isEmpty();
    verify(redisSubscriber, timeout(2000).times(1)).onMessage(any(), any());
  }

  @Test
  @DisplayName("TRADE 알림 - DB 저장 후 RedisSubscriber가 메시지 처리")
  void TRADE_알림은_DB저장_후_Subscriber가_메시지를_받는다() {
    // given
    Member sender = memberRepository.findById(SENDER_ID).orElseThrow();
    Member receiver = memberRepository.findById(RECEIVER_ID).orElseThrow();
    CreateNotiCommand command = DEFAULT_TRADE_NOTI(sender.getId(), receiver.getId());

    // when
    notiService.createNotificationProcess(command);

    // then
    Notification saved = notiRepository.findAll().iterator().next();
    assertThat(saved.getType()).isEqualTo(TRADE);
    assertThat(saved.getReferenceId()).isEqualTo("1");
    assertThat(saved.getReceiverId()).isEqualTo(RECEIVER_ID);
    String expectedBody = sender.getNickname() + "님과의 거래 상태가 '" + command.body() + "'상태로 변경되었습니다.";
    assertThat(saved.getBody()).isEqualTo(expectedBody);
    verify(redisSubscriber, timeout(2000).times(1)).onMessage(any(), any());
  }
}
