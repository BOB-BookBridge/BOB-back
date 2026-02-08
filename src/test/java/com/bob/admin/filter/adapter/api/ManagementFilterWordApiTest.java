package com.bob.admin.filter.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.admin.filter.adapter.api.request.CreateFilterWordRequest;
import com.bob.infrastructure.data.filter.model.FilterWord;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("관리자 - 금칙어 관리 API 테스트")
@BobApiTest
record ManagementFilterWordApiTest(
    MockMvcTester tester, ObjectMapper objectMapper,

    FilterWordRepository wordRepository
) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 금칙어_목록_조회() {
        wordRepository.save(createFilterWord("금칙어1"));
        wordRepository.save(createFilterWord("금칙어2"));

        MvcTestResult result = tester.get().uri("/management/filter-words")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful();
    }

    @Test
    void 금칙어_생성() throws Exception {
        var request = new CreateFilterWordRequest("금칙어");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = tester.post().uri("/management/filter-words")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.result", AssertThatUtils.equalsTo("CREATED"));

        assertThat(wordRepository.existsByWord("금칙어")).isTrue();
    }

    @Test
    void 금칙어_생성_중복_키워드_실패() throws Exception {
        wordRepository.save(createFilterWord("중복키워드"));

        var request = new CreateFilterWordRequest("중복키워드");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = tester.post().uri("/management/filter-words")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus4xxClientError();
    }

    @Test
    void 금칙어_삭제() {
        FilterWord filterWord = wordRepository.save(createFilterWord("삭제금칙어"));

        MvcTestResult result = tester.delete().uri("/management/filter-words/{wordId}", filterWord.getId())
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.result", AssertThatUtils.equalsTo("DELETED"));

        assertThat(wordRepository.findById(filterWord.getId())).isEmpty();
    }

    @Test
    void 금칙어_삭제_존재하지_않는_ID_실패() {
        MvcTestResult result = tester.delete().uri("/management/filter-words/{wordId}", 999999L)
            .exchange();

        assertThat(result)
            .hasStatus5xxServerError();
    }

    private FilterWord createFilterWord(String word) {
        return FilterWord.builder()
            .word(word)
            .predefined(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
