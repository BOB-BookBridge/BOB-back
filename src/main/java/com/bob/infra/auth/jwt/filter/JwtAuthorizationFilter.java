package com.bob.infra.auth.jwt.filter;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.exception.response.AuthenticationError.IS_EXPIRED_TOKEN;
import static com.bob.global.utils.web.CookieUtils.getCookie;
import static com.bob.global.utils.web.CookieUtils.removeCookie;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.bob.infra.auth.response.MemberDetails;
import com.bob.infra.config.registry.OptionalRegistry;
import com.bob.infra.config.registry.PermitAllRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private static final String ACCESS_COOKIE_NAME = "AUTHORIZATION";
  private static final String REFRESH_COOKIE_NAME = "REFRESH_KEY";

  private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
  private final PermitAllRegistry registry;
  private final OptionalRegistry optionalRegistry;
  private final JwtProvider jwtProvider;

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

    MemberDetails memberDetails = new MemberDetails(jwtProvider.getMemberId(accessToken));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        memberDetails, null, memberDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private boolean isAuthentication(String accessToken) {
    return accessToken != null
        && jwtProvider.isVerified(accessToken)
        && !jwtProvider.isExpired(accessToken);
  }

  private void setErroneousAuthenticationExceptionBody(HttpServletRequest request, HttpServletResponse response,
      String accessToken) throws IOException {
    // 토큰이 존재하지 않거나 유효하지 않은 토큰
    if (accessToken == null || !jwtProvider.isVerified(accessToken)) {
      jwtAuthEntryPoint.commence(request, response, new ApplicationAuthenticationException(FAILED_AUTHENTICATION));
      return;
    }

    // 유효한 토큰이 존재하지만 만료된 토큰
    if (jwtProvider.isExpired(accessToken)) {
      jwtAuthEntryPoint.commence(request, response, new ApplicationAuthenticationException(IS_EXPIRED_TOKEN));
    }
  }
}
