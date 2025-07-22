package com.bob.domain.post.service.dto.command;

public record ChangePostStatusCommand(
    Long postId,
    String status
) {

}
