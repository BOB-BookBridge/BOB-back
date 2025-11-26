package com.bob.security.config.registry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("요청 URL 화이트리스트 테스트")
@ExtendWith(MockitoExtension.class)
class PermitAllRegistryTest {

    @InjectMocks
    private PermitAllRegistry registry;

    @Test
    void permitAll_포함() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/email");
        request.setMethod("POST");

        boolean result = registry.isWhiteList(request);

        assertThat(result).isTrue();
    }

    @Test
    void permitAll_미포함() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/posts");
        request.setMethod("DELETE");

        boolean result = registry.isWhiteList(request);

        assertThat(result).isFalse();
    }
}
