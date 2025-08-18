package com.bob.infra.auth.service;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import com.bob.infra.auth.service.port.AuthMemberPort;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@DisplayName("소셜 로그인 인증 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthServiceTest {

  @InjectMocks
  SocialAuthService socialAuthService;

  @Mock
  OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

  @Mock
  AuthMemberPort memberPort;

  @Test
  @DisplayName("구글 로그인 테스트")
  void 구글_소셜_로그인() {
    // given
    ClientRegistration registration = googleRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> attrs = Map.of("sub", "1", "email", "test@google.com", "name", "foo");
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "sub");
    given(delegate.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(eq("GOOGLE"), eq("test@google.com"), eq("foo"))).willReturn(MEMBER_ID);

    // when
    OAuth2User principal = socialAuthService.loadUser(request);

    // then
    then(memberPort).should(times(1)).socialLoginProcess("GOOGLE", "test@google.com", "foo");
    assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
    assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
  }

  @Test
  @DisplayName("네이버 로그인 테스트")
  void 네이버_소셜_로그인() {
    // given
    ClientRegistration registration = naverRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> response = Map.of("id", "1", "email", "test@naver.com", "nickname", "foo");
    Map<String, Object> attrs = Map.of("response", response);
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "response");
    given(delegate.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(eq("NAVER"), eq("test@naver.com"), eq("foo"))).willReturn(MEMBER_ID);

    // when
    OAuth2User principal = socialAuthService.loadUser(request);

    // then
    then(memberPort).should(times(1)).socialLoginProcess("NAVER", "test@naver.com", "foo");
    assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
    assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
  }

  @Test
  @DisplayName("지원하지 않는 소셜 서비스 로그인 테스트")
  void 지원하지_않는_provider는_예외를_발생시킨다() {
    // given
    ClientRegistration registration = kakaoRegistration();
    OAuth2UserRequest request = requestOf(registration);

    // when & then
    assertThatThrownBy(() -> socialAuthService.loadUser(request))
        .isInstanceOf(OAuth2AuthenticationException.class)
        .hasMessage("해당 로그인 방법은 지원하지 않습니다.");
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
        .scope("name", "email", "nickname")
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