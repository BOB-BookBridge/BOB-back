package com.bob.admin.notice.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.admin.notice.adapter.api.request.RegisterBannerRequest;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.NoticeType;
import com.bob.admin.notice.domain.repository.NoticeRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("공지 API 테스트")
@BobApiTest
record NoticeApiTest(MockMvcTester tester, NoticeRepository noticeRepository, ObjectMapper objectMapper) {

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
        setAuthentication();
    }

    @Test
    void 게시용_공지_등록() throws Exception {
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        var request = new RegisterBannerRequest("content", endTime);
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = tester.post().uri("/notices/banner")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.result", AssertThatUtils.equalsTo("CREATED"));

        Iterable<Notice> notices = noticeRepository.findAll();
        assertThat(notices).hasSize(1);

        Notice notice = notices.iterator().next();
        assertThat(notice.getType()).isEqualTo(NoticeType.BANNER);
        assertThat(notice.getWriterId()).isEqualTo(MANAGER_ID);
        assertThat(notice.getTitle()).isEqualTo("[공지사항]");
        assertThat(notice.getContent()).isEqualTo("content");
        assertThat(notice.getEndsAt()).isEqualTo(endTime);
    }

    @Test
    void 게시용_공지_조회() {
        noticeRepository.save(Notice.createBanner(MANAGER_ID, "old", LocalDateTime.now().plusHours(2)));
        noticeRepository.save(Notice.createBanner(MANAGER_ID, "new", LocalDateTime.now().plusHours(1)));

        SecurityContextHolder.clearContext();

        MvcTestResult result = tester.get().uri("/notices/banner")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.title", AssertThatUtils.equalsTo("[공지사항]"))
            .hasPathSatisfying("$.content", AssertThatUtils.equalsTo("new"))
            .hasPathSatisfying("$.writer.id", AssertThatUtils.equalsTo(MANAGER_ID.toString()))
            .hasPathSatisfying("$.writer.nickname", AssertThatUtils.notNull());
    }

    @Test
    void 게시용_공지_없으면_204_반환() {
        SecurityContextHolder.clearContext();

        MvcTestResult result = tester.get().uri("/notices/banner")
            .exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void 활성화_되어있는_게시용_공지_비활성화() {
        noticeRepository.save(Notice.createBanner(MANAGER_ID, "content", LocalDateTime.now().plusHours(2)));

        MvcTestResult result = tester.patch().uri("/notices/banner")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.result", AssertThatUtils.equalsTo("UPDATED"));

        assertThat(noticeRepository.findCurrentBanner(LocalDateTime.now(), PageRequest.of(0, 1))).isEmpty();
    }

    private void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}
