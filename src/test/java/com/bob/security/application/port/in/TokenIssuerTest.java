package com.bob.security.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.support.annotation.ContainerTest;

@DisplayName("토큰 발급자 테스트")
@ContainerTest
class TokenIssuerTest {

    @Autowired
    private TokenIssuer tokenIssuer;

    @Autowired
    private AuthCachePort cachePort;

    @Autowired
    private TokenManager tokenManager;

    @Value("${jwt.access-name}")
    private String accessName;

    @Value("${jwt.refresh-name}")
    private String refreshName;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 토큰_재발급() {
        String refreshKey = "old-refresh-key";

        request.setCookies(new Cookie(refreshName, refreshKey));
        cachePort.setRefreshKey(refreshKey, MEMBER_ID.toString());

        tokenIssuer.reissue(request, response);

        Cookie[] cookies = response.getCookies();
        assertThat(cookies).hasSize(2);

        Cookie accessCookie = findCookie(cookies, accessName);
        Cookie refreshCookie = findCookie(cookies, refreshName);

        assertThat(accessCookie).isNotNull();
        assertThat(refreshCookie).isNotNull();
        assertThat(refreshCookie.getValue()).isNotEqualTo(refreshKey);

        String newRefreshKey = refreshCookie.getValue();
        assertThat(cachePort.get(newRefreshKey)).isPresent();
        assertThat(cachePort.get(refreshKey)).isEmpty();
    }

    @Test
    void 토큰_재발급_헤더가_없다면_예외가_발생한다() {
        request.setCookies();

        assertThatThrownBy(() -> tokenIssuer.reissue(request, response))
            .isInstanceOf(ApplicationAuthenticationException.class)
            .hasMessage(AuthenticationError.REFRESH_KEY_EXPIRED.getMessage());
    }

    @Test
    void 토큰_재발급_헤더의_값이_키_저장소에_존재하지_않으면_예외가_발생한다() {
        String nonExistentKey = "non-existent-key";
        request.setCookies(new Cookie(refreshName, nonExistentKey));

        assertThatThrownBy(() -> tokenIssuer.reissue(request, response))
            .isInstanceOf(ApplicationAuthenticationException.class)
            .hasMessage(AuthenticationError.REFRESH_KEY_EXPIRED.getMessage());
    }

    private Cookie findCookie(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name))
                return cookie;
        }
        return null;
    }
}
