package com.bob.security.application;

import static com.bob.support.fixture.auth.port.dto.SocialAuthMemberFixture.createActiveSocialAuthMember;
import static com.bob.support.fixture.auth.port.dto.SocialAuthMemberFixture.createBannedSocialAuthMember;
import static com.bob.support.fixture.auth.port.dto.SocialAuthMemberFixture.createDeactivatedSocialAuthMember;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.security.application.port.out.MemberLoader;

@DisplayName("소셜 로그인 인증 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {

    @InjectMocks
    OAuth2Service oAuth2Service;

    @Mock
    DefaultOAuth2UserService oAuth2UserService;

    @Mock
    MemberLoader memberLoader;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(oAuth2Service, "oAuth2UserService", oAuth2UserService);
    }

    @Test
    void 구글_소셜_로그인() {
        ClientRegistration registration = googleRegistration();
        OAuth2UserRequest request = requestOf(registration);
        Map<String, Object> attrs = Map.of("sub", "1", "email", "test@google.com", "name", "foo");
        OAuth2User loadedUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attrs,
            "sub"
        );
        given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
        given(memberLoader.load(eq("GOOGLE"), eq("test@google.com"), eq("foo")))
            .willReturn(createActiveSocialAuthMember());

        OAuth2User principal = oAuth2Service.loadUser(request);

        then(memberLoader).should(times(1)).load("GOOGLE", "test@google.com", "foo");
        assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
        assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
    }

    @Test
    void 네이버_소셜_로그인() {
        ClientRegistration registration = naverRegistration();
        OAuth2UserRequest request = requestOf(registration);
        Map<String, Object> response = Map.of("id", "1", "email", "test@naver.com", "nickname", "foo");
        Map<String, Object> attrs = Map.of("response", response);
        OAuth2User loadedUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attrs,
            "response"
        );
        given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
        given(memberLoader.load(eq("NAVER"), eq("test@naver.com"), eq("foo")))
            .willReturn(createActiveSocialAuthMember());

        OAuth2User principal = oAuth2Service.loadUser(request);

        then(memberLoader).should(times(1)).load("NAVER", "test@naver.com", "foo");
        assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
        assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
    }

    @Test
    void 미지원_소셜_로그인() {
        ClientRegistration registration = kakaoRegistration();
        OAuth2UserRequest request = requestOf(registration);

        assertThatThrownBy(() -> oAuth2Service.loadUser(request))
            .isInstanceOf(OAuth2AuthenticationException.class)
            .hasMessage("해당 로그인 방법은 지원하지 않습니다.");
    }

    @Test
    void 탈퇴계정_예외_발생() {
        ClientRegistration registration = googleRegistration();
        OAuth2UserRequest request = requestOf(registration);
        Map<String, Object> attrs = Map.of("sub", "1", "email", "test@google.com", "name", "tester");
        OAuth2User loadedUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attrs,
            "sub"
        );
        given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
        given(memberLoader.load(anyString(), anyString(), anyString())).willReturn(createDeactivatedSocialAuthMember());

        assertThatThrownBy(() -> oAuth2Service.loadUser(request))
            .isInstanceOf(ApplicationAuthenticationException.class)
            .hasMessage(AuthenticationError.MEMBER_DEACTIVATED.getMessage());
    }

    @Test
    void 제재계정_예외_발생() {
        ClientRegistration registration = naverRegistration();
        OAuth2UserRequest request = requestOf(registration);
        Map<String, Object> response = Map.of("id", "1", "email", "test@naver.com", "nickname", "tester");
        Map<String, Object> attrs = Map.of("response", response);
        OAuth2User loadedUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attrs,
            "response"
        );
        given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
        given(memberLoader.load(anyString(), anyString(), anyString())).willReturn(createBannedSocialAuthMember());

        assertThatThrownBy(() -> oAuth2Service.loadUser(request))
            .isInstanceOf(ApplicationAuthenticationException.class)
            .hasMessage(AuthenticationError.MEMBER_BANNED.getMessage());
    }

    private static OAuth2UserRequest requestOf(ClientRegistration registration) {
        final OAuth2AccessToken token = new OAuth2AccessToken(
            BEARER,
            "access-token",
            Instant.now().minusSeconds(10),
            Instant.now().plusSeconds(3600)
        );
        return new OAuth2UserRequest(registration, token);
    }

    private static ClientRegistration googleRegistration() {
        return ClientRegistration
            .withRegistrationId("google")
            .clientId("id")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/google")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
            .userNameAttributeName("sub")
            .scope("openid", "email", "profile")
            .build();
    }

    private static ClientRegistration naverRegistration() {
        return ClientRegistration
            .withRegistrationId("naver")
            .clientId("id")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/naver")
            .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
            .tokenUri("https://nid.naver.com/oauth2.0/token")
            .userInfoUri("https://openapi.naver.com/v1/nid/me")
            .userNameAttributeName("response")
            .scope("email", "nickname")
            .build();
    }

    /**
     * 지원하지 않는 provider (kakao)
     */
    private static ClientRegistration kakaoRegistration() {
        return ClientRegistration
            .withRegistrationId("kakao")
            .clientId("id")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/kakao")
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .tokenUri("https://kauth.kakao.com/oauth/token")
            .userInfoUri("https://kapi.kakao.com/v2/user/me")
            .userNameAttributeName("id")
            .scope("profile", "account_email")
            .build();
    }
}
