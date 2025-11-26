package com.bob.core.application.member.dto.command;

public record SocialLoginCommand(String provider, String email, String nickname, Integer emdId) {

    public static SocialLoginCommand of(String provider, String email, String nickname) {
        return new SocialLoginCommand(provider, email, nickname, 213);
    }
}
