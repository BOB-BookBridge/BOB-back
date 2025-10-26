package com.bob.domain.post.service.dto.command;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ChangePostCommand(
    Long postId,
    UUID memberId,
    Integer sellPrice,
    String bookStatus,
    String description,
    Boolean wishOnly
) {

}
