package com.bob.core.application.chat.port.result;

import java.util.UUID;

public record ChatMember(UUID id, String nickname, String profileImageUrl) {

    public static ChatMember of(UUID id, String nickname, String profileImageUrl) {
        return new ChatMember(id, nickname, profileImageUrl);
    }
}
