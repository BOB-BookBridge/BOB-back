package com.bob.core.post.application.event;

import static com.bob.core.post.domain.status.Status.ACTIVE;
import static com.bob.core.post.domain.status.Status.DEACTIVATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.member.event.MemberRecoveredEvent;
import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.post.application.port.in.PostModifier;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;
import com.bob.core.report.event.ReportPostProcessedEvent;
import com.bob.core.trade.event.TradeStatusChangedEvent;

@DisplayName("게시글 이벤트 처리 테스트")
@ExtendWith(MockitoExtension.class)
class PostEventHandlerTest {

    @InjectMocks
    private PostEventHandler postEventHandler;

    @Mock
    private PostReader postReader;

    @Mock
    private PostModifier postModifier;

    @Test
    void 회원_복구_이벤트_처리() {
        UUID memberId = UUID.randomUUID();
        var event = new MemberRecoveredEvent(memberId);

        postEventHandler.handleMemberRecovered(event);

        var captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
        then(postModifier).should(times(1)).changeStatusByAccountEvent(captor.capture());
        assertThat(captor.getValue().memberId()).isEqualTo(memberId);
        assertThat(captor.getValue().status()).isEqualTo(ACTIVE);
    }

    @Test
    void 회원_비활성화_이벤트_처리() {
        UUID memberId = UUID.randomUUID();
        var event = new MemberDeactivatedEvent(memberId);

        postEventHandler.handleMemberDeactivated(event);

        var captor = ArgumentCaptor.forClass(ChangeMemberPostStatusCommand.class);
        then(postModifier).should(times(1)).changeStatusByAccountEvent(captor.capture());
        assertThat(captor.getValue().memberId()).isEqualTo(memberId);
        assertThat(captor.getValue().status()).isEqualTo(DEACTIVATED);
    }

    @Test
    void 게시글_관련_거래_상태_변경_처리() {
        Long postId = 1L;
        Long tradeId = 1L;

        var event = new TradeStatusChangedEvent(postId, tradeId, "COMPLETED");

        postEventHandler.handleTradeStatusChanged(event);

        var captor = ArgumentCaptor.forClass(ChangePostTradeProgressCommand.class);
        then(postModifier).should(times(1)).changePostTradeProgress(eq(event.postId()), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo("COMPLETED");
    }

    @Test
    void 신고_게시글_제재_이벤트_처리() {
        Long postId = 1L;
        Post post = mock(Post.class);
        var event = new ReportPostProcessedEvent(postId);
        given(postReader.read(postId)).willReturn(post);

        postEventHandler.handlePostReportProcessed(event);

        then(postReader).should(times(1)).read(eq(event.targetId()));
        then(post).should(times(1)).deactivate();
    }
}
