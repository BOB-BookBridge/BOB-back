package com.bob.security.application.port.dto;

import java.util.UUID;

public record SocialAuthMember(UUID id, String status) {

    public static SocialAuthMember of(UUID id, String status) {
        return new SocialAuthMember(id, status);
    }
}
