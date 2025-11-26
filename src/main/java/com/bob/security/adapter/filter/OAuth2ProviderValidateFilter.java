package com.bob.security.adapter.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2ProviderValidateFilter extends OncePerRequestFilter {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${oauth.entrance}")
    private String entrance;

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return !servletPath.startsWith(entrance + "/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
        ServletException,
        IOException {
        String servletPath = request.getServletPath();
        String registrationId = servletPath.substring((entrance + "/").length());
        ClientRegistration client = clientRegistrationRepository.findByRegistrationId(registrationId);

        if (client == null) {
            String encodedCause = URLEncoder.encode("unsupported_provider_" + registrationId, StandardCharsets.UTF_8);
            String target = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/error")
                .queryParam("cause", encodedCause)
                .build(true)
                .toUriString();

            response.sendRedirect(target);
            return;
        }

        chain.doFilter(request, response);
    }
}
