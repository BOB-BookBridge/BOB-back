package com.bob.admin.post.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.admin.post.application.dto.command.ProcessManagementPostStatusCommand;
import com.bob.admin.post.domain.PostManagementHistory;
import com.bob.admin.post.domain.repository.PostManagementHistoryRepository;
import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.status.Status;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.post.domain.PostFixture;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("관리자 - 게시글 처리 테스트")
@ContainerTest
@RequiredArgsConstructor
class ManagementPostProcessorTest {

    final ManagementPostProcessor postProcessor;
    final PostManagementHistoryRepository historyRepository;

    final PostRepository postRepository;
    final ReportRepository reportRepository;

    @MockitoBean
    final ApplicationEventPublisher eventPublisher;

    @Test
    void 승인_처리() {
        Post post = postRepository.save(PostFixture.createPendingPost());
        assertThat(post.getStatus()).isEqualTo(Status.PENDING);

        var command = new ProcessManagementPostStatusCommand(MANAGER_ID, "ACTIVE", null);

        postProcessor.process(post.getId(), command);

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);

        PostManagementHistory history = historyRepository.findByPostId(post.getId()).orElseThrow();
        assertThat(history.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(history.getStatus().name()).isEqualTo("ACTIVE");
        assertThat(history.getMemo()).isNull();
        assertThat(history.getProcessedAt()).isNotNull();
    }

    @Test
    void 비활성화_처리() {
        Post post = postRepository.save(PostFixture.createPendingPost());
        assertThat(post.getStatus()).isEqualTo(Status.PENDING);

        var command = new ProcessManagementPostStatusCommand(MANAGER_ID, "DEACTIVATED", null);

        postProcessor.process(post.getId(), command);

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.DEACTIVATED);

        PostManagementHistory history = historyRepository.findByPostId(post.getId()).orElseThrow();
        assertThat(history.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(history.getStatus().name()).isEqualTo("DEACTIVATED");
    }

    @Test
    void 제재_처리() {
        Post post = postRepository.save(PostFixture.createPendingPost());
        var command = new ProcessManagementPostStatusCommand(MANAGER_ID, "BANNED", null);

        postProcessor.process(post.getId(), command);

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(Status.BANNED);

        PostManagementHistory history = historyRepository.findByPostId(post.getId()).orElseThrow();
        assertThat(history.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(history.getStatus().name()).isEqualTo("BANNED");
    }

    @Test
    void 제재_처리_시_회원의_누적_신고_횟수가_3회_이상이면_회원_비활성화() {
        Post post = postRepository.save(PostFixture.createPendingPost(OTHER_MEMBER_ID));

        reportRepository.save(ReportFixture.createProcessedReport());
        reportRepository.save(ReportFixture.createProcessedReport());

        var command = new ProcessManagementPostStatusCommand(MANAGER_ID, "BANNED", null);

        postProcessor.process(post.getId(), command);

        then(eventPublisher).should().publishEvent(any(MemberDeactivatedEvent.class));
    }
}
