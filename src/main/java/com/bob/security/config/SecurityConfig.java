package com.bob.security.config;

import static com.bob.global.utils.web.CookieUtils.removeCookie;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.global.ratelimit.repository.RateLimitRepository;
import com.bob.security.adapter.entrypoint.TokenAuthenticationEntryPoint;
import com.bob.security.adapter.filter.LoginFilter;
import com.bob.security.adapter.filter.OAuth2ProviderValidateFilter;
import com.bob.security.adapter.filter.TokenAuthorizationFilter;
import com.bob.security.adapter.handler.AccessDeniedHandler;
import com.bob.security.adapter.handler.OAuth2FailureHandler;
import com.bob.security.adapter.handler.OAuth2SuccessHandler;
import com.bob.security.application.MemberDetailsService;
import com.bob.security.application.OAuth2Service;
import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.props.HeaderProperties;
import com.bob.security.config.registry.OptionalRegistry;
import com.bob.security.config.registry.PermitAllRegistry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(HeaderProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/oauth2/**", "/login/oauth2/**",
        "/h2-console/**", "/error/**",
    };

    private final MemberDetailsService memberDetailsService;
    private final AuthCachePort cachePort;

    private final OAuth2Service oAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2ProviderValidateFilter OAuth2ProviderValidateFilter;

    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenAuthorizationFilter tokenAuthorizationFilter;
    private final TokenManager tokenManager;

    private final AccessDeniedHandler accessDeniedHandler;

    private final RateLimitRepository rateLimitRepository;

    private final HeaderProperties headerProperties;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager,
        PermitAllRegistry permitAllRegistry, OptionalRegistry optionalRegistry
    ) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .anonymous(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth -> oauth
                .authorizationEndpoint(p -> p.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(p -> p.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(p -> p.userService(oAuth2Service))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            .logout(filter -> filter
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    removeCookie(res, "AUTHORIZATION");
                    removeCookie(res, "REFRESH_KEY");
                    res.setStatus(SC_OK);
                })
            )
            .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(request -> request
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers(permitAllRegistry.asArray()).permitAll() // 인증 필터 우회
                .requestMatchers(optionalRegistry.asArray()).permitAll() // 토큰 존재 시 인증 필터 사용
                .anyRequest().authenticated()
            )
            .addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(OAuth2ProviderValidateFilter, OAuth2AuthorizationRequestRedirectFilter.class)
            .addFilterAt(loginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(memberDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://bookbridge.kr"
        ));
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private LoginFilter loginFilter(AuthenticationManager authenticationManager) {
        LoginFilter loginFilter = new LoginFilter(
            authenticationManager, tokenAuthenticationEntryPoint,
            cachePort, tokenManager, rateLimitRepository, headerProperties, objectMapper
        );
        loginFilter.setFilterProcessesUrl("/auth/login");
        loginFilter.setPostOnly(true);
        return loginFilter;
    }
}
