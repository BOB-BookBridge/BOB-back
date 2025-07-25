package com.bob.domain.noti.service;

import static com.bob.support.fixture.command.CreateNotiCommandFixture.DEFAULT_TEXT_CHAT_NOTI;
import static com.bob.support.fixture.command.CreateNotiCommandFixture.DEFAULT_TRADE_NOTI;
import static com.bob.support.fixture.domain.noti.NotificationFixture.CUSTOM_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.repository.NotiRepository;
import com.bob.domain.notification.service.NotiService;
import com.bob.domain.notification.service.dto.command.ChangeNotificationReadStatusCommand;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.service.dto.response.NotificationsResponse;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.infra.redis.subscriber.RedisSubscriber;
import com.bob.support.TestContainerSupport;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
    AtomicInteger before = new AtomicInteger();
    notiRepository.findAll().forEach(n -> before.getAndIncrement());

    // when
    notiService.createNotificationProcess(command);

    // then
    assertThat(notiRepository.findAll()).hasSize(before.get());
    verify(redisSubscriber, timeout(2000).times(1)).onMessage(any(), any());
  }

  @Test
  @DisplayName("TRADE 알림 - DB 저장 후 RedisSubscriber가 메시지 처리")
  void TRADE_알림은_DB저장_후_Subscriber가_메시지를_받는다() {
    // given
    Member sender = memberRepository.findById(SENDER_ID).orElseThrow();
    Member receiver = memberRepository.findById(RECEIVER_ID).orElseThrow();
    CreateNotiCommand command = DEFAULT_TRADE_NOTI(sender.getId(), receiver.getId());
    AtomicInteger before = new AtomicInteger();
    notiRepository.findAll().forEach(n -> before.getAndIncrement());

    // when
    notiService.createNotificationProcess(command);

    // then
    assertThat(notiRepository.findAll()).hasSize(before.get() + 1);
    verify(redisSubscriber, timeout(2000).times(1)).onMessage(any(), any());
  }

  @Test
  @DisplayName("최근 2주 이내 알림만 조회된다")
  void 알림_목록_조회_시_최근_2주_알림만_조회된다() {
    // given (테스트 컨테이너 초기화 시 2주 이내 알림 2개, 15일전 알림 1개 저장)
    Member receiver = memberRepository.findById(RECEIVER_ID).orElseThrow();
    ReadNotificationsQuery query = ReadNotificationsQuery.of(receiver.getId());

    // when
    NotificationsResponse response = notiService.readNotificationsProcess(query);

    // then
    assertThat(response.notifications()).hasSize(2);
    assertThat(response.notifications())
        .extracting("body")
        .containsExactlyInAnyOrder("최근 알림1", "최근 알림2");

    LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
    assertThat(response.notifications())
        .allSatisfy(noti -> assertThat(noti.createdAt()).isAfter(twoWeeksAgo));
  }

  @Test
  @DisplayName("알림 읽음 처리 - 성공 테스트")
  void 알림_소유자는_읽음처리를_할_수_있다() {
    // given
    Notification notification = notiRepository.save(CUSTOM_NOTIFICATION("1", RECEIVER_ID, "body", false));
    assertThat(notification.getIsRead()).isFalse();
    ChangeNotificationReadStatusCommand command = ChangeNotificationReadStatusCommand.of(RECEIVER_ID, notification.getId());

    // when
    notiService.changeNotificationReadStatusProcess(command);

    // then
    Notification updated = notiRepository.findById(notification.getId()).orElseThrow();
    assertThat(updated.getIsRead()).isTrue();
  }

  @Test
  @DisplayName("알림 읽음 처리 - 실패 테스트 (소유자 아님)")
  void 알림_읽음처리는_소유자가_아니면_예외가_발생한다() {
    // given
    Notification notification = notiRepository.save(CUSTOM_NOTIFICATION("1", RECEIVER_ID, "body", false));
    ChangeNotificationReadStatusCommand command = ChangeNotificationReadStatusCommand.of(SENDER_ID, notification.getId());

    // when & then
    assertThatThrownBy(() -> notiService.changeNotificationReadStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOTIFICATION_ACCESS_DENIED.getMessage());
  }
}
