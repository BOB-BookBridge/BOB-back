package com.bob.infra.config.registry;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class PermitAllRegistry {

  private final List<RequestMatcher> whitelistMatchers = List.of(
      new AntPathRequestMatcher("/h2-console/**"), new AntPathRequestMatcher("/error/**"),
      new AntPathRequestMatcher("/dummy", POST.name()), // TODO : 개발 종료 시 삭제
      new AntPathRequestMatcher("/auth/**", POST.name()),
      new AntPathRequestMatcher("/members", POST.name()),
      new AntPathRequestMatcher("/ai/**", GET.name()),
      new AntPathRequestMatcher("/members/{memberId:\\d+}", GET.name()),
      new AntPathRequestMatcher("/posts", GET.name()),
      new AntPathRequestMatcher("/members/temp/**", PATCH.name()),
      new AntPathRequestMatcher("/members/recover", PATCH.name())
  );

  public boolean isWhiteList(HttpServletRequest request) {
    return whitelistMatchers.stream()
        .anyMatch(matcher -> matcher.matches(request));
  }
}
