package com.bob.core.bookcase.application.dto.command;

public record FreeUsageByRefIdCommand(Long usageId) {

    public static FreeUsageByRefIdCommand of(Long usageId) {
        return new FreeUsageByRefIdCommand(usageId);
    }
}
