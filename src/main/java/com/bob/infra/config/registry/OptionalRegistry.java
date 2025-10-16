package com.bob.infra.config.registry;

import static org.springframework.http.HttpMethod.GET;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class OptionalRegistry {
  private final List<RequestMatcher> optionalAuthMatchers = List.of(
      new AntPathRequestMatcher("/areas/**", HttpMethod.PATCH.name()),
      new AntPathRequestMatcher("/posts", GET.name()),
      new AntPathRequestMatcher("/posts/{postId:\\d+}", GET.name())
  );

  public boolean isOptionalAuth(HttpServletRequest request) {
    return optionalAuthMatchers.stream().anyMatch(m -> m.matches(request));
  }
}
