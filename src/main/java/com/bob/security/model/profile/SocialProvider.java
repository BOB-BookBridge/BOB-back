package com.bob.security.model.profile;

public record SocialProvider(Provider provider, String providerId, String email, String nickname) {

}
