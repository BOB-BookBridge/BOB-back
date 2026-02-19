package com.bob.security.config.registry;

import static com.bob.global.utils.uuid.UuidUtils.UUID_V7_REGEX;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Component
public class PermitAllRegistry {

    private final List<RequestMatcher> matchers;

    public PermitAllRegistry(HandlerMappingIntrospector spector) {
        matchers = List.of(
            mvc(spector, POST, "/inquiries"),
            mvc(spector, POST, "/auth/**"),
            mvc(spector, POST, "/members"),
            mvc(spector, GET, "/members/{memberId:" + UUID_V7_REGEX + '}'),
            mvc(spector, GET, "/notices/banner"),
            mvc(spector, PATCH, "/members/temp/password"),
            mvc(spector, PATCH, "/members/recover"),

            mvc(spector, GET, "/actuator/**") // 서버 보안 그룹 허용 ip 직접 접근
        );
    }

    private MvcRequestMatcher mvc(HandlerMappingIntrospector introspector, HttpMethod method, String pattern) {
        MvcRequestMatcher matcher = new MvcRequestMatcher(introspector, pattern);
        matcher.setMethod(method);
        return matcher;
    }

    public boolean isWhiteList(HttpServletRequest request) {
        return matchers.stream().anyMatch(m -> m.matches(request));
    }

    public RequestMatcher[] asArray() {
        return matchers.toArray(RequestMatcher[]::new);
    }
}
