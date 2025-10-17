package com.bob.infra.config.registry;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Component
public class OptionalRegistry {

  private final List<RequestMatcher> matchers;

  public OptionalRegistry(HandlerMappingIntrospector introspector) {
    matchers = List.of(
        mvc(introspector, PATCH, "/areas/**"),
        mvc(introspector, GET, "/posts/**")
    );
  }

  private MvcRequestMatcher mvc(HandlerMappingIntrospector introspector, HttpMethod method, String pattern) {
    MvcRequestMatcher matcher = new MvcRequestMatcher(introspector, pattern);
    matcher.setMethod(method);
    return matcher;
  }

  public boolean isOptionalAuth(HttpServletRequest request) {
    return matchers.stream().anyMatch(m -> m.matches(request));
  }

  public RequestMatcher[] asArray() {
    return matchers.toArray(RequestMatcher[]::new);
  }
}
