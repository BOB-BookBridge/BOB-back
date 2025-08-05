package com.bob.infra.auth.jwt.filter;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.utils.web.CookieUtils.getCookie;

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

    if (optionalRegistry.isOptionalAuth(request) && accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!isAuthentication(accessToken)) {
      jwtAuthEntryPoint.commence(request, response, new ApplicationAuthenticationException(FAILED_AUTHENTICATION));
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
}
