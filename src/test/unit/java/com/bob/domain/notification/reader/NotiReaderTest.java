package com.bob.domain.notification.reader;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.noti.NotificationFixture.DEFAULT_NOTIFICATIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.repository.NotiRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("NotiReader 단위 테스트")
@ExtendWith(MockitoExtension.class)
class NotiReaderTest {

  @InjectMocks
  private NotiReader notiReader;

  @Mock
  private NotiRepository notiRepository;

  @Test
  @DisplayName("알림을 조회할 수 있다")
  void 알림을_조회할_수_있다() {
    // given
    List<Notification> expected = DEFAULT_NOTIFICATIONS();
    given(notiRepository.findByReceiverIdAndCreatedAtAfter(any(), any())).willReturn(expected);

    // when
    List<Notification> result = notiReader.readNotifications(MEMBER_ID);

    // then
    assertThat(result).hasSize(2);
    ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
    then(notiRepository).should().findByReceiverIdAndCreatedAtAfter(eq(MEMBER_ID), captor.capture());

    LocalDateTime captured = captor.getValue();
    LocalDateTime now = LocalDateTime.now();
    assertThat(captured).isAfter(now.minusWeeks(2).minusSeconds(2));
    assertThat(captured).isBefore(now.minusWeeks(2).plusSeconds(2));
  }
}
