package com.bob.security.adapter.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@DisplayName("접근 거부 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class AccessDeniedHandlerTest {

    @InjectMocks
    private AccessDeniedHandler deniedHandler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(deniedHandler, "objectMapper", objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 요청_거부_403() throws Exception {
        request.setRequestURI("/management/members");
        AccessDeniedException exception = new AccessDeniedException("접근 권한이 없습니다.");

        deniedHandler.handle(request, response, exception);

        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value()),
            () -> assertThat(response.getContentType()).isEqualTo("application/json; charset=UTF-8"),
            () -> assertThat(response.getContentAsString()).contains("접근 권한이 없습니다.")
        );
    }
}
