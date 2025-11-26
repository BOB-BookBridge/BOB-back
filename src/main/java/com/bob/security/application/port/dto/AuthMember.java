package com.bob.security.application.port.dto;

import java.util.UUID;

public record AuthMember(UUID id, String email, String password, String status) {

    public static AuthMember of(UUID id, String email, String password, String status) {
        return new AuthMember(id, email, password, status);
    }
}
