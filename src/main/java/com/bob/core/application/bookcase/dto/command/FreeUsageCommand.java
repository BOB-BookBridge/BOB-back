package com.bob.core.application.bookcase.dto.command;

import java.util.UUID;

public record FreeUsageCommand(UUID memberId) {

    public static FreeUsageCommand of(UUID memberId) {
        return new FreeUsageCommand(memberId);
    }
}
