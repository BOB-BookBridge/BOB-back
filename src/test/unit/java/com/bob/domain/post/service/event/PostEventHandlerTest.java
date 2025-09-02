package com.bob.domain.post.service.event;

import static com.bob.domain.post.entity.status.Status.ACTIVE;
import static com.bob.domain.post.entity.status.Status.REMOVED;
import static com.bob.domain.post.entity.status.Status.WITHHELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.command.ChangeMemberPostStatusCommand;
import com.bob.global.event.application.dto.member.AccountEvent;
import com.bob.global.event.application.dto.member.type.AccountEventType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PostEventHandler 테스트")
@ExtendWith(MockitoExtension.class)
class PostEventHandlerTest {

  @InjectMocks
  private PostEventHandler postEventHandler;

  @Mock
  private PostService postService;

  @Test
  void 계정_복구_이벤트() {
    // given
    UUID memberId = UUID.randomUUID();
    AccountEvent event = AccountEvent.of(memberId, AccountEventType.RECOVER);

    // when
    postEventHandler.handleAccountEvent(event);

    // then
    ArgumentCaptor<ChangeMemberPostStatusCommand> captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
    then(postService).should(times(1)).changeStatusByAccountEventProcess(captor.capture());
    assertThat(captor.getValue().memberId()).isEqualTo(memberId);
    assertThat(captor.getValue().status()).isEqualTo(ACTIVE);
  }

  @Test
  void 계정_탈퇴_이벤트() {
    // given
    UUID memberId = UUID.randomUUID();
    AccountEvent event = AccountEvent.of(memberId, AccountEventType.WITHDRAW);

    // when
    postEventHandler.handleAccountEvent(event);

    // then
    ArgumentCaptor<ChangeMemberPostStatusCommand> captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
    then(postService).should(times(1)).changeStatusByAccountEventProcess(captor.capture());
    assertThat(captor.getValue().memberId()).isEqualTo(memberId);
    assertThat(captor.getValue().status()).isEqualTo(REMOVED);
  }
}
