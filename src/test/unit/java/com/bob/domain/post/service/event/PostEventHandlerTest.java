package com.bob.domain.post.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.command.WithholdPostStatusCommand;
import com.bob.global.event.application.dto.RemoveMemberEvent;
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
  void 회원_탈퇴_이벤트_처리_테스트() {
    // given
    UUID memberId = UUID.randomUUID();
    RemoveMemberEvent event = RemoveMemberEvent.of(memberId);

    // when
    postEventHandler.handleMemberRemoveEvent(event);

    // then
    ArgumentCaptor<WithholdPostStatusCommand> captor = ArgumentCaptor.forClass(WithholdPostStatusCommand.class);
    then(postService).should(times(1)).withholdPostProcess(captor.capture());
    assertThat(captor.getValue().memberId()).isEqualTo(memberId);
  }
}
