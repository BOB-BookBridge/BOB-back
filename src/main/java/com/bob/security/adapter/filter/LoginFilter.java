package com.bob.security.adapter.filter;

import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.addCookie;
import static com.bob.global.utils.web.CookieUtils.getCookie;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.global.ratelimit.repository.RateLimitRepository;
import com.bob.security.adapter.filter.request.LoginRequest;
import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.props.HeaderProperties;
import com.bob.security.model.MemberDetails;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthCachePort authCachePort;

    private final TokenManager tokenManager;

    private final RateLimitRepository rateLimitRepository;

    private final HeaderProperties headerProperties;

    private final ObjectMapper objectMapper;

    /* @formatter:off */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        checkRateLimit(request);

        LoginRequest loginRequest = readLoginData(request);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        MemberDetails principal = (MemberDetails)authentication.getPrincipal();
        UUID memberId = principal.id();
        String accessToken = tokenManager.create(memberId.toString());
        String refreshKey = generateCode(32);
        authCachePort.updateRefreshKey(getCookie(request, headerProperties.refreshName()), refreshKey, memberId.toString());
        addCookie(response, headerProperties.accessName(), accessToken, headerProperties.refreshTokenExpireTime());
        addCookie(response, headerProperties.refreshName(), refreshKey, headerProperties.refreshTokenExpireTime());
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if (failed instanceof ApplicationAuthenticationException)
            authenticationEntryPoint.commence(request, response, failed);
        else
            authenticationEntryPoint.commence(request, response, new ApplicationAuthenticationException(setAuthenticationError(failed)));
    }

    private static AuthenticationError setAuthenticationError(AuthenticationException ex) {
        if (ex instanceof DisabledException)
            return AuthenticationError.IS_DEACTIVATED_MEMBER;

        return AuthenticationError.FAILED_AUTHENTICATION;
    }

    private LoginRequest readLoginData(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* @formatter:on */

    private void checkRateLimit(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String rateLimitKey = "login:" + clientIp;

        boolean allowed = rateLimitRepository.isAllowed(rateLimitKey, 60, 5);

        if (!allowed) {
            long waitSeconds = rateLimitRepository.getWaitForRefill(rateLimitKey, 60, 5);
            String message = "로그인 시도가 너무 많습니다. " + waitSeconds + "초 후 다시 시도해주세요.";

            throw new ApplicationAuthenticationException(AuthenticationError.LOGIN_RATE_LIMIT_EXCEEDED, message);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();

        return ip;
    }
}
