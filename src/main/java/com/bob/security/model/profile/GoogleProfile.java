package com.bob.security.model.profile;

import java.util.Map;

public class GoogleProfile {

    public static SocialProvider from(Map<String, Object> attrs) {
        return new SocialProvider(
            Provider.GOOGLE,
            (String)attrs.get("sub"),
            (String)attrs.get("email"),
            (String)attrs.getOrDefault("name", ((String)attrs.get("email")).split("@")[0])
        );
    }
}
