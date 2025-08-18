package com.bob.infra.auth.oauth.provider;

import java.util.Map;

public class GoogleProfile {

  public static SocialProvider from(Map<String, Object> attrs) {
    return new SocialProvider(
        SocialProvider.Provider.GOOGLE,
        (String) attrs.get("sub"),
        (String) attrs.get("email"),
        (String) attrs.getOrDefault("name", ((String) attrs.get("email")).split("@")[0])
    );
  }
}
