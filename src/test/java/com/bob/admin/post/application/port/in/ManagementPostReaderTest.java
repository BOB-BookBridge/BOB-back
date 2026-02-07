package com.bob.admin.post.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPendingPost;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.admin.post.application.port.result.ManagementPostDetail;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.admin.post.domain.ManagementStatus;
import com.bob.admin.post.domain.PostManagementHistory;
import com.bob.admin.post.domain.repository.PostManagementHistoryRepository;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.status.Status;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("관리자 - 게시글 조회 테스트")
@ContainerTest
record ManagementPostReaderTest(
    ManagementPostReader postReader, PostManagementHistoryRepository historyRepository,
    PostRepository postRepository, ReportRepository reportRepository
) {

    @Test
    void 게시글_목록_조회() {
        postRepository.save(createPendingPost());

        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll(null, null, pageable);

        assertThat(result.totalCount()).isGreaterThanOrEqualTo(1);
        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> {
                assertThat(post.id()).isNotNull();
                assertThat(post.title()).isNotNull();
                assertThat(post.writer().email()).isNotNull();
                assertThat(post.status()).isIn("PENDING", "BANNED");
                assertThat(post.createdAt()).isNotNull();
            });
    }

    @Test
    void 보류된_관리_게시글_목록_조회() {
        postRepository.save(createPendingPost());

        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll(null, "PENDING", pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> assertThat(post.status()).isEqualTo("PENDING"));
    }

    @Test
    void 제재된_관리_게시글_목록_조회() {
        Post post = createPost();
        ReflectionTestUtils.setField(post, "status", Status.BANNED);
        postRepository.save(post);

        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll(null, "BANNED", pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post1 -> assertThat(post1.status()).isEqualTo("BANNED"));
    }

    @Test
    void 이메일_기반_게시글_목록_조회() {
        postRepository.save(createPendingPost(MEMBER_ID));

        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll("test@test.com", null, pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> assertThat(post.writer().email()).isEqualTo("test@test.com"));
    }

    @Test
    void 이메일_상태_기반_복합_게시글_목록_조회() {
        postRepository.save(createPendingPost(MEMBER_ID));

        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll("test@test.com", "PENDING", pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> {
                assertThat(post.writer().email()).isEqualTo("test@test.com");
                assertThat(post.status()).isEqualTo("PENDING");
            });
    }

    @Test
    void 게시글_목록_조회_결과_없음() {
        var pageable = PageRequest.of(0, 20);

        ManagementPostSummaries result = postReader.readAll(null, "BANNED", pageable);

        assertThat(result.totalCount()).isZero();
        assertThat(result.posts()).isEmpty();
    }

    @Test
    void 게시글_상세_조회() {
        Post post = createPendingPost();
        postRepository.save(post);

        ManagementPostDetail detail = postReader.readDetail(post.getId());

        assertThat(detail.id()).isEqualTo(post.getId());
        assertThat(detail.status()).isEqualTo("PENDING");
        assertThat(detail.title()).isEqualTo(post.getTitle());
        assertThat(detail.writer()).isNotNull();
        assertThat(detail.writer().email()).isEqualTo("test@test.com");
        assertThat(detail.reports()).isNotNull();
        assertThat(detail.reports().count()).isZero();
        assertThat(detail.managerNickname()).isNull();
        assertThat(detail.previousStatus()).isNull();
        assertThat(detail.processedAt()).isNull();
    }

    @Test
    void 게시글_상세_조회_신고_내역_포함() {
        Post post = createPendingPost();
        postRepository.save(post);

        reportRepository.save(
            Report.createReport(ReportTarget.POST, post.getId(), "욕설/비방", OTHER_MEMBER_ID, MEMBER_ID));
        reportRepository.save(Report.createReport(ReportTarget.POST, post.getId(), "스팸", OTHER_MEMBER_ID, MEMBER_ID));

        ManagementPostDetail detail = postReader.readDetail(post.getId());

        assertThat(detail.reports().count()).isEqualTo(2);
        assertThat(detail.reports().reasons()).containsExactlyInAnyOrder("욕설/비방", "스팸");
    }

    @Test
    void 게시글_상세_조회_관리_이력_포함() {
        Post post = createPendingPost();
        postRepository.save(post);

        PostManagementHistory history = PostManagementHistory.create(
            post.getId(), MANAGER_ID, ManagementStatus.PENDING, ManagementStatus.ACTIVE, null
        );
        historyRepository.save(history);

        ManagementPostDetail detail = postReader.readDetail(post.getId());

        assertThat(detail.managerNickname()).isEqualTo("manager");
        assertThat(detail.previousStatus()).isEqualTo("PENDING");
        assertThat(detail.processedAt()).isNotNull();
    }
}
