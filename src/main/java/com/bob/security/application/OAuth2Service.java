package com.bob.security.application;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.security.application.port.dto.SocialAuthMember;
import com.bob.security.application.port.out.MemberLoader;
import com.bob.security.model.profile.GoogleProfile;
import com.bob.security.model.profile.NaverProfile;
import com.bob.security.model.profile.SocialProvider;

@Component
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();

    private final MemberLoader memberLoader;

    /* @formatter:off */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final String provider = userRequest.getClientRegistration().getRegistrationId();
        verifyProvider(provider);

        final OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        final Map<String, Object> attrs = oAuth2User.getAttributes();
        final SocialProvider profile = switch (provider) {
            case "google" -> GoogleProfile.from(attrs);
            case "naver" -> NaverProfile.from(attrs);
            default -> throw new OAuth2AuthenticationException(new OAuth2Error("000"), "해당 로그인 방법은 지원하지 않습니다.");
        };

        SocialAuthMember member = memberLoader.load(provider.toUpperCase(), profile.email(), profile.nickname());

        switch (member.status()) {
            case "DEACTIVATED" -> throw new ApplicationAuthenticationException(AuthenticationError.IS_DEACTIVATED_MEMBER) {};
            case "BANNED" -> throw new ApplicationAuthenticationException(AuthenticationError.IS_BANNED_MEMBER) {};
            default -> {
                final Map<String, Object> principalAttrs = Map.of("memberId", member.id().toString());
                return new DefaultOAuth2User(null, principalAttrs, "memberId");
            }
        }
    }
    /* @formatter:on */

    private static void verifyProvider(String provider) {
        if (!"google".equalsIgnoreCase(provider) && !"naver".equalsIgnoreCase(provider))
            throw new OAuth2AuthenticationException(new OAuth2Error("OE01"), "해당 로그인 방법은 지원하지 않습니다.");
    }
}
