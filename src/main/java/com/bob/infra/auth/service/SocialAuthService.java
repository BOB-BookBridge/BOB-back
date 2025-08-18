package com.bob.infra.auth.service;

import com.bob.infra.auth.oauth.provider.GoogleProfile;
import com.bob.infra.auth.oauth.provider.NaverProfile;
import com.bob.infra.auth.oauth.provider.SocialProvider;
import com.bob.infra.auth.service.port.AuthMemberPort;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2userService;
  private final AuthMemberPort memberPort;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    final String provider = userRequest.getClientRegistration().getRegistrationId();
    verifyProvider(provider);

    final OAuth2User oAuth2User = oAuth2userService.loadUser(userRequest);
    final Map<String, Object> attrs = oAuth2User.getAttributes();
    final SocialProvider profile = switch (provider) {
      case "google" -> GoogleProfile.from(attrs);
      case "naver" -> NaverProfile.from(attrs);
      default -> throw new OAuth2AuthenticationException(new OAuth2Error("000"), "해당 로그인 방법은 지원하지 않습니다.");
    };

    final UUID memberId = memberPort.socialLoginProcess(provider.toUpperCase(), profile.email(), profile.nickname());
    final Map<String, Object> principalAttrs = Map.of("memberId", memberId.toString());
    return new DefaultOAuth2User(null, principalAttrs, "memberId");
  }

  private static void verifyProvider(String provider) {
    if (!"google".equalsIgnoreCase(provider) && !"naver".equalsIgnoreCase(provider)) {
      throw new OAuth2AuthenticationException(new OAuth2Error("001"), "해당 로그인 방법은 지원하지 않습니다.");
    }
  }
}
