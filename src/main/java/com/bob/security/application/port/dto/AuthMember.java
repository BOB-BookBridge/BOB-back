package com.bob.security.application.port.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record AuthMember(UUID id, String email, String password, String status, String role) {

}
