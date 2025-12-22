package com.bob.core.area.adapter.api;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.area.adapter.api.request.AuthenticateAreaRequest;
import com.bob.support.annotation.BobApiTest;

@DisplayName("지역 API 테스트")
@BobApiTest
@RequiredArgsConstructor
class AreaApiTest {

    final MockMvcTester mvcTester;

    final ObjectMapper objectMapper;

    @Test
    void 위치_인증() throws JsonProcessingException {
        AuthenticateAreaRequest request = new AuthenticateAreaRequest(213, 37.4998, 127.0402);
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/areas/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();
    }
}
