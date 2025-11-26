package com.bob.security.adapter.handler;

import static com.bob.support.fixture.auth.CookieFixture.ACCESS_TOKEN;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_NAME;
import static com.bob.support.fixture.auth.CookieFixture.REFRESH_COOKIE_NAME;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.props.HeaderProperties;

@DisplayName("소셜 로그인 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @InjectMocks
    private OAuth2SuccessHandler successHandler;

    @Mock
    private AuthCachePort cachePort;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private HeaderProperties headerProperties;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "baseUrl", "https://base");
        given(headerProperties.accessName()).willReturn(AUTH_COOKIE_NAME);
        given(headerProperties.refreshName()).willReturn(REFRESH_COOKIE_NAME);
        given(headerProperties.refreshTokenExpireTime()).willReturn(1L);
        given(tokenManager.create(MEMBER_ID.toString())).willReturn(ACCESS_TOKEN);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 소셜_로그인() throws Exception {
        String memberId = MEMBER_ID.toString();
        var authentication = new UsernamePasswordAuthenticationToken(memberId, null);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        then(tokenManager).should(times(1)).create(MEMBER_ID.toString());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(cachePort).should(times(1)).setRefreshKey(captor.capture(), eq(memberId));

        String issuedRefreshKey = captor.getValue();
        assertThat(issuedRefreshKey).isNotBlank();
        assertThat(issuedRefreshKey.length()).isEqualTo(32);

        Cookie[] cookies = response.getCookies();
        assertThat(cookies).isNotNull();

        Cookie accessCookie = Arrays.stream(cookies)
            .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
            .findFirst()
            .orElseThrow();

        Cookie refreshCookie = Arrays.stream(cookies)
            .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
            .findFirst()
            .orElseThrow();

        assertThat(accessCookie.getValue()).isEqualTo(ACCESS_TOKEN);
        assertThat(refreshCookie.getValue()).isEqualTo(issuedRefreshKey);
        assertThat(response.getRedirectedUrl()).isEqualTo("https://base");
    }
}
