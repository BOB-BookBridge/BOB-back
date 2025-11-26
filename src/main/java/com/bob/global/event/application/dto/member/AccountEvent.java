package com.bob.global.event.application.dto.member;

import java.util.UUID;

import com.bob.global.event.application.dto.member.type.AccountEventType;

public record AccountEvent(
    UUID memberId,
    AccountEventType type
) {

    public static AccountEvent of(UUID memberId, AccountEventType type) {
        return new AccountEvent(memberId, type);
    }
}
