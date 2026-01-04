package com.bob.core.member.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.member.dto.command.RegisterMemberWishCommandFixture.createRegisterMemberWishCommand;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.member.adapter.api.request.CreateMemberWishRequest;
import com.bob.core.member.adapter.api.response.MemberWishResponse;
import com.bob.core.member.application.port.in.MemberWishManager;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.encoder.PasswordEncoder;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("회원 희망 도서 API 테스트")
@BobApiTest
record MemberWishApiTest(
    MockMvcTester mvcTester, MemberRepository memberRepository, PasswordEncoder passwordEncoder,
    MemberWishManager memberWishManager, ObjectMapper objectMapper, EntityManager entityManager
) {

    void setAuthentication(UUID memberId) {
        MemberDetails principal = new MemberDetails(memberId, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void 희망_도서_등록() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        var request = new CreateMemberWishRequest(
            "0000000000000", "희망도서", "저자",
            "설명", 13000, "https://cover.jpg", LocalDate.of(2013, 12, 24)
        );

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/members/wishes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatus(201);

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getWishes()).isNotEmpty();
    }

    @Test
    void 희망_도서_조회() throws Exception {
        Member member = memberRepository.save(createMember());

        memberWishManager.registerWish(member.getId(), createRegisterMemberWishCommand("0000000000000"));

        MvcTestResult result = mvcTester.get()
            .uri("/members/{memberId}/wishes", member.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        List<MemberWishResponse> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<>() {
            }
        );

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).title()).isEqualTo("제목");
    }

    @Test
    void 희망_도서_삭제() {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        memberWishManager.registerWish(member.getId(), createRegisterMemberWishCommand());

        entityManager.flush();
        entityManager.clear();

        member = memberRepository.findById(member.getId()).orElseThrow();
        Long wishId = member.getWishes().get(0).getId();

        MvcTestResult result = mvcTester.delete()
            .uri("/members/wishes/{wishId}", wishId)
            .exchange();

        assertThat(result).hasStatusOk();

        member = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(member.getWishes()).isEmpty();
    }
}
