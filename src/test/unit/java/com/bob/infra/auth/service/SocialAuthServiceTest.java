package com.bob.infra.auth.service;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.response.oauth.SocialLoginResponseFixture.BANNED_MEMBER_RESPONSE;
import static com.bob.support.fixture.response.oauth.SocialLoginResponseFixture.WITHDRAWN_MEMBER_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import com.bob.domain.member.service.dto.response.SocialLoginResponse;
import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.infra.auth.service.port.AuthMemberPort;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("소셜 로그인 인증 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthServiceTest {

  @InjectMocks
  SocialAuthService socialAuthService;

  @Mock
  DefaultOAuth2UserService oAuth2UserService;

  @Mock
  AuthMemberPort memberPort;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(socialAuthService, "oAuth2UserService", oAuth2UserService);
  }

  @Test
  void 구글_소셜_로그인() {
    // given
    ClientRegistration registration = googleRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> attrs = Map.of("sub", "1", "email", "test@google.com", "name", "foo");
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "sub");
    given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(eq("GOOGLE"), eq("test@google.com"), eq("foo"))).willReturn(SocialLoginResponse.from(defaultIdMember()));

    // when
    OAuth2User principal = socialAuthService.loadUser(request);

    // then
    then(memberPort).should(times(1)).socialLoginProcess("GOOGLE", "test@google.com", "foo");
    assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
    assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
  }

  @Test
  void 네이버_소셜_로그인() {
    // given
    ClientRegistration registration = naverRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> response = Map.of("id", "1", "email", "test@naver.com", "nickname", "foo");
    Map<String, Object> attrs = Map.of("response", response);
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "response");
    given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(eq("NAVER"), eq("test@naver.com"), eq("foo"))).willReturn(SocialLoginResponse.from(defaultIdMember()));

    // when
    OAuth2User principal = socialAuthService.loadUser(request);

    // then
    then(memberPort).should(times(1)).socialLoginProcess("NAVER", "test@naver.com", "foo");
    assertThat(principal.getName()).isEqualTo(MEMBER_ID.toString());
    assertThat(principal.getAttributes()).containsEntry("memberId", MEMBER_ID.toString());
  }

  @Test
  void 미지원_소셜_로그인() {
    // given
    ClientRegistration registration = kakaoRegistration();
    OAuth2UserRequest request = requestOf(registration);

    // when & then
    assertThatThrownBy(() -> socialAuthService.loadUser(request))
        .isInstanceOf(OAuth2AuthenticationException.class)
        .hasMessage("해당 로그인 방법은 지원하지 않습니다.");
  }

  @Test
  void 탈퇴계정_예외_발생() {
    // given
    ClientRegistration registration = googleRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> attrs = Map.of("sub", "1", "email", "test@google.com", "name", "tester");
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "sub");
    given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(anyString(), anyString(), anyString())).willReturn(WITHDRAWN_MEMBER_RESPONSE);

    // when & then
    assertThatThrownBy(() -> socialAuthService.loadUser(request))
        .isInstanceOf(ApplicationAuthenticationException.class)
        .hasMessage(AuthenticationError.IS_WITHDRAWN_MEMBER.getMessage());
  }

  @Test
  void 제재계정_예외_발생() {
    // given
    ClientRegistration registration = naverRegistration();
    OAuth2UserRequest request = requestOf(registration);
    Map<String, Object> response = Map.of("id", "1", "email", "test@naver.com", "nickname", "tester");
    Map<String, Object> attrs = Map.of("response", response);
    OAuth2User loadedUser = new DefaultOAuth2User(null, attrs, "response");
    given(oAuth2UserService.loadUser(request)).willReturn(loadedUser);
    given(memberPort.socialLoginProcess(anyString(), anyString(), anyString())).willReturn(BANNED_MEMBER_RESPONSE);

    // when & then
    assertThatThrownBy(() -> socialAuthService.loadUser(request))
        .isInstanceOf(ApplicationAuthenticationException.class)
        .hasMessage(AuthenticationError.IS_BANNED_MEMBER.getMessage());
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