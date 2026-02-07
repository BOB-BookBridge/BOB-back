package com.bob.admin.post.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPendingPost;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.admin.post.application.port.result.ManagementPostDetail;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.status.Status;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("관리자 - 게시글 관리 API 테스트")
@BobApiTest
record ManagementPostApiTest(MockMvcTester mvcTester, ObjectMapper objectMapper, PostRepository postRepository,
    ReportRepository reportRepository) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 게시글_목록_조회() throws JsonProcessingException, UnsupportedEncodingException {
        postRepository.save(createPendingPost());

        MvcTestResult result = mvcTester.get().uri("/management/posts")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.posts", AssertThatUtils.notNull())
            .hasPathSatisfying("$.totalCount", AssertThatUtils.notNull());

        ManagementPostSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementPostSummaries.class);

        assertThat(response.totalCount()).isNotZero();
        assertThat(response.posts()).isNotEmpty();
    }

    @Test
    void 게시글_목록_조회_상태_필터링_PENDING() throws JsonProcessingException, UnsupportedEncodingException {
        postRepository.save(createPendingPost());

        MvcTestResult result = mvcTester.get()
            .uri("/management/posts?status=PENDING")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.posts", AssertThatUtils.notNull());

        ManagementPostSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementPostSummaries.class);

        assertThat(response.posts()).isNotEmpty();
        assertThat(response.posts())
            .allSatisfy(post -> assertThat(post.status()).isEqualTo("PENDING"));
    }

    @Test
    void 게시글_목록_조회_상태_필터링_BANNED() throws JsonProcessingException, UnsupportedEncodingException {
        Post post = createPost();
        ReflectionTestUtils.setField(post, "status", Status.BANNED);
        postRepository.save(post);

        MvcTestResult result = mvcTester.get()
            .uri("/management/posts?status=BANNED")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.posts", AssertThatUtils.notNull());

        ManagementPostSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementPostSummaries.class);

        assertThat(response.posts()).isNotEmpty();
        assertThat(response.posts())
            .allSatisfy(post1 -> assertThat(post1.status()).isEqualTo("BANNED"));
    }

    @Test
    void 게시글_상세_조회() {
        Post post = createPendingPost();
        postRepository.save(post);

        MvcTestResult result = mvcTester.get()
            .uri("/management/posts/{postId}", post.getId())
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.notNull())
            .hasPathSatisfying("$.status", AssertThatUtils.notNull())
            .hasPathSatisfying("$.title", AssertThatUtils.notNull())
            .hasPathSatisfying("$.writer", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reports", AssertThatUtils.notNull());
    }

    @Test
    void 게시글_상세_조회_신고_내역_포함() throws JsonProcessingException, UnsupportedEncodingException {
        Post post = createPendingPost();
        postRepository.save(post);

        reportRepository.save(Report.createReport(ReportTarget.POST, post.getId(), "욕설/비방", OTHER_MEMBER_ID, MEMBER_ID));

        MvcTestResult result = mvcTester.get()
            .uri("/management/posts/{postId}", post.getId())
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful();

        ManagementPostDetail detail =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementPostDetail.class);

        assertThat(detail.reports().count()).isEqualTo(1);
        assertThat(detail.reports().reasons()).containsExactly("욕설/비방");
    }

    @Test
    void 게시글_목록_조회_이메일_필터링() throws JsonProcessingException, UnsupportedEncodingException {
        postRepository.save(createPendingPost(MEMBER_ID));

        MvcTestResult result = mvcTester.get()
            .uri("/management/posts?email=test@test.com")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.posts", AssertThatUtils.notNull());

        ManagementPostSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementPostSummaries.class);

        assertThat(response.posts()).isNotEmpty();
        assertThat(response.posts())
            .allSatisfy(post -> assertThat(post.writer().email()).isEqualTo("test@test.com"));
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
