package com.bob.security.adapter.handler;

import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.addCookie;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.props.HeaderProperties;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.base-url}")
    private String baseUrl;

    private final AuthCachePort cachePort;

    private final TokenManager tokenManager;

    private final HeaderProperties headerProperties;

    /* @formatter:off */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String memberId = authentication.getName();
        String accessToken = tokenManager.create(memberId);
        String refreshKey = generateCode(32);

        cachePort.setRefreshKey(refreshKey, memberId);

        addCookie(response, headerProperties.accessName(), accessToken, headerProperties.refreshTokenExpireTime());
        addCookie(response, headerProperties.refreshName(), refreshKey, headerProperties.refreshTokenExpireTime());

        response.sendRedirect(baseUrl);
    }
    /* @formatter:on */
}
