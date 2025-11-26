package com.bob.security.model.profile;

import java.util.Map;

public class NaverProfile {

    @SuppressWarnings("unchecked")
    public static SocialProvider from(Map<String, Object> attrs) {
        Map<String, Object> response = (Map<String, Object>)attrs.get("response");
        return new SocialProvider(
            Provider.NAVER,
            (String)response.get("id"),
            (String)response.get("email"),
            (String)response.getOrDefault("nickname", ((String)response.get("email")).split("@")[0])
        );
    }
}
