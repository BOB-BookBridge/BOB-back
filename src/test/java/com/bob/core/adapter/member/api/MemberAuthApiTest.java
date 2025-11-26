package com.bob.core.adapter.member.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.adapter.member.api.request.SendAuthenticationCodeRequest;
import com.bob.core.adapter.member.api.request.VerifyMailRequest;
import com.bob.core.application.member.port.out.MemberCachePort;
import com.bob.support.annotation.BobApiTest;

@DisplayName("회원 인증 API 테스트")
@BobApiTest
record MemberAuthApiTest(MockMvcTester mvcTester, MemberCachePort memberCachePort, ObjectMapper objectMapper) {

    @Test
    void 인증_코드_전송() throws JsonProcessingException {
        String email = "auth@test.com";
        var request = new SendAuthenticationCodeRequest(email);

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/auth/email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 메일_인증() throws JsonProcessingException {
        String email = "verify@test.com";
        String code = "123456";

        memberCachePort.setAuthenticationCode(email, code);

        var request = new VerifyMailRequest(email, code);
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/auth/email/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();
        assertThat(memberCachePort.checkAuthenticationSuccess(email)).isTrue();
    }
}
