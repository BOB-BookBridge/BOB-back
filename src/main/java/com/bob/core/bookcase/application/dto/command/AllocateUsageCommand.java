package com.bob.core.bookcase.application.dto.command;

import java.util.UUID;

public record AllocateUsageCommand(UUID memberId, Long usageId) {

    public static AllocateUsageCommand of(UUID memberId, Long usageId) {
        return new AllocateUsageCommand(memberId, usageId);
    }

    public static AllocateUsageCommand of(Long usageId) {
        return new AllocateUsageCommand(null, usageId);
    }
}
