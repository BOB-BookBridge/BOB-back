package com.bob.core.application.post.event;

import static com.bob.core.domain.post.status.Status.ACTIVE;
import static com.bob.core.domain.post.status.Status.DEACTIVATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.application.post.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.application.post.port.in.PostModifier;
import com.bob.global.event.application.dto.member.AccountEvent;
import com.bob.global.event.application.dto.member.type.AccountEventType;

@DisplayName("게시글 이벤트 처리 테스트")
@ExtendWith(MockitoExtension.class)
class PostEventHandlerTest {

    @InjectMocks
    private PostEventHandler postEventHandler;

    @Mock
    private PostModifier postModifier;

    @Test
    void 계정_복구_이벤트() {
        UUID memberId = UUID.randomUUID();
        AccountEvent event = AccountEvent.of(memberId, AccountEventType.RECOVER);

        postEventHandler.handleAccountEvent(event);

        var captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
        then(postModifier).should(times(1)).changeStatusByAccountEvent(captor.capture());
        assertThat(captor.getValue().memberId()).isEqualTo(memberId);
        assertThat(captor.getValue().status()).isEqualTo(ACTIVE);
    }

    @Test
    void 계정_탈퇴_이벤트() {
        UUID memberId = UUID.randomUUID();
        AccountEvent event = AccountEvent.of(memberId, AccountEventType.DEACTIVATE);

        postEventHandler.handleAccountEvent(event);

        var captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
        then(postModifier).should(times(1)).changeStatusByAccountEvent(captor.capture());
        assertThat(captor.getValue().memberId()).isEqualTo(memberId);
        assertThat(captor.getValue().status()).isEqualTo(DEACTIVATED);
    }
}
