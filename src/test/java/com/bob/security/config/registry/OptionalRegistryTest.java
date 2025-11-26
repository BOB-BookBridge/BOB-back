package com.bob.security.config.registry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("선택적 인증 URL 테스트")
@ExtendWith(MockitoExtension.class)
class OptionalRegistryTest {

    @InjectMocks
    private OptionalRegistry registry;

    @Test
    void optional_포함() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/areas/authentication");
        request.setMethod("POST");

        boolean result = registry.isOptionalAuth(request);

        assertThat(result).isTrue();
    }

    @Test
    void optional_미포함() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/posts");
        request.setMethod("POST");

        boolean result = registry.isOptionalAuth(request);

        assertThat(result).isFalse();
    }
}
