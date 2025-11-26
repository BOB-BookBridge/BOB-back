package com.bob.core.adapter.member.api;

import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LAT;
import static com.bob.support.fixture.area.domain.AreaFixture.CENTER_LON;
import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.adapter.member.api.request.ChangeMemberProfileImageRequest;
import com.bob.core.adapter.member.api.request.ChangePasswordRequest;
import com.bob.core.adapter.member.api.request.ChangeProfileRequest;
import com.bob.core.adapter.member.api.request.IssuePasswordRequest;
import com.bob.core.adapter.member.api.request.RecoverAccountRequest;
import com.bob.core.adapter.member.api.request.SignupRequest;
import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.port.out.MemberCachePort;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.PasswordEncoder;
import com.bob.core.domain.member.repository.MemberRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("회원 API 테스트")
@BobApiTest
record MemberApiTest(
    MockMvcTester mvcTester, MemberRepository memberRepository, MemberCachePort memberCachePort,
    PasswordEncoder passwordEncoder, ObjectMapper objectMapper
) {

    @Test
    void 회원가입() throws JsonProcessingException {
        String email = "signtest@example.com";
        memberCachePort.setAuthenticationSuccess(email);

        SignupRequest request = new SignupRequest("tester", email, "Password123", EMD_AREA_ID);

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatus(201);

        Member saved = memberRepository.findByEmail(email).orElseThrow();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getNickname()).isEqualTo("tester");
    }

    @Test
    void 내_프로필_조회() throws Exception {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        MvcTestResult result = mvcTester.get()
            .uri("/members/me")
            .exchange();

        assertThat(result).hasStatusOk();

        MemberDetail response = objectMapper.readValue(result.getResponse().getContentAsString(), MemberDetail.class);

        assertThat(response.id()).isEqualTo(member.getId());
    }

    @Test
    void 회원_프로필_조회() throws Exception {
        Member member = memberRepository.save(createMember());

        MvcTestResult result = mvcTester.get()
            .uri("/members/{memberId}", member.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        MemberDetail response = objectMapper.readValue(result.getResponse().getContentAsString(), MemberDetail.class);

        assertThat(response.id()).isEqualTo(member.getId());
        assertThat(response.nickname()).isEqualTo(member.getNickname());
    }

    @Test
    void 프로필_정보_수정() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        List<String> interests = List.of("소설", "에세이");
        var request = new ChangeProfileRequest("새로운닉네임", EMD_AREA_ID, true, CENTER_LAT, CENTER_LON, interests);

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/members/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getNickname()).isEqualTo("새로운닉네임");
    }

    @Test
    void 비밀번호_변경() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        member.updatePassword(passwordEncoder.encode("OldPassword123"));
        setAuthentication(member.getId());

        var request = new ChangePasswordRequest("OldPassword123", "NewPassword123");

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/members/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("NewPassword123", updated.getPassword())).isTrue();
    }

    @Test
    void 임시_비밀번호_발급() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        String originalPassword = member.getPassword();

        var request = new IssuePasswordRequest(member.getEmail());
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/members/temp/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getPassword()).isNotEqualTo(originalPassword);
    }

    @Test
    void 프로필_이미지_변경() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        var request = new ChangeMemberProfileImageRequest("https://new-profile-image.jpg");

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/members/me/image")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getProfileImageUrl()).isEqualTo("https://new-profile-image.jpg");
    }

    @Test
    void 회원_복구() throws JsonProcessingException {
        Member member = memberRepository.save(createMember());
        member.deactivate();

        var request = new RecoverAccountRequest(member.getEmail());
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/members/recover")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.isDeactivated()).isFalse();
    }

    @Test
    void 회원_탈퇴() {
        Member member = memberRepository.save(createMember());
        setAuthentication(member.getId());

        MvcTestResult result = mvcTester.delete()
            .uri("/members/me")
            .exchange();

        assertThat(result).hasStatusOk();

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.isDeactivated()).isTrue();
    }

    void setAuthentication(UUID memberId) {
        MemberDetails principal = new MemberDetails(memberId, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
