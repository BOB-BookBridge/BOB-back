package com.bob.core.bookcase.application.dto.command;

import java.util.UUID;

public record DeleteBookcaseItemCommand(UUID memberId) {

    public static DeleteBookcaseItemCommand of(UUID memberId) {
        return new DeleteBookcaseItemCommand(memberId);
    }
}
