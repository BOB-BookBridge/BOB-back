package com.bob.core.application.bookcase.dto.command;

public record FreeUsageByRefIdCommand(Long usageId) {

    public static FreeUsageByRefIdCommand of(Long usageId) {
        return new FreeUsageByRefIdCommand(usageId);
    }
}
