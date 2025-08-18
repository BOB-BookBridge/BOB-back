package com.bob.infra.auth.oauth.provider;

public record SocialProvider(
    Provider provider,
    String providerId,
    String email,
    String nickname
) {
  public enum Provider { GOOGLE, NAVER }
}