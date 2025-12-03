package com.bob.security.adapter.filter;

import static com.bob.global.exception.response.AuthenticationError.ACCESS_TOKEN_EXPIRED;
import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.utils.web.CookieUtils.getCookie;
import static com.bob.global.utils.web.CookieUtils.removeCookie;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.security.adapter.entrypoint.TokenAuthenticationEntryPoint;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.registry.OptionalRegistry;
import com.bob.security.config.registry.PermitAllRegistry;
import com.bob.security.model.MemberDetails;

@Component
@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String ACCESS_COOKIE_NAME = "AUTHORIZATION";
    private static final String REFRESH_COOKIE_NAME = "REFRESH_KEY";

    private final TokenAuthenticationEntryPoint jwtAuthEntryPoint;

    private final TokenManager tokenManager;

    private final PermitAllRegistry registry;
    private final OptionalRegistry optionalRegistry;

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /* @formatter:off */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (registry.isWhiteList(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getCookie(request, ACCESS_COOKIE_NAME);

        if (optionalRegistry.isOptionalAuth(request)) {
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            } else if (!isAuthentication(accessToken)) {
                removeCookie(response, ACCESS_COOKIE_NAME);
                removeCookie(response, REFRESH_COOKIE_NAME);
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (!isAuthentication(accessToken)) {
            setErroneousAuthenticationExceptionBody(request, response, accessToken);
            return;
        }

        MemberDetails memberDetails = new MemberDetails(tokenManager.getClaim(accessToken), true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isAuthentication(String accessToken) {
        return accessToken != null
            && tokenManager.verify(accessToken)
            && !tokenManager.expire(accessToken);
    }

    private void setErroneousAuthenticationExceptionBody(HttpServletRequest request, HttpServletResponse response, String accessToken) throws IOException {
        // 토큰이 존재하지 않거나 유효하지 않은 토큰
        if (accessToken == null || !tokenManager.verify(accessToken)) {
            jwtAuthEntryPoint.commence(request, response, new ApplicationAuthenticationException(FAILED_AUTHENTICATION));
            return;
        }

        // 유효한 토큰이 존재하지만 만료된 토큰
        if (tokenManager.expire(accessToken))
            jwtAuthEntryPoint.commence(request, response, new ApplicationAuthenticationException(ACCESS_TOKEN_EXPIRED));
    }
    /* @formatter:on */
}
