package com.bob.core.application.post.dto.command;

import java.util.UUID;

public record ChangePostInfoCommand(
    UUID memberId, String bookStatus, String description, Boolean wishOnly
) {

}
