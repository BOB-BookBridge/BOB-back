package com.bob.domain.post.service.dto.command;

public record ChangeTradeProgressCommand(
    Long postId,
    String status
) {

}
