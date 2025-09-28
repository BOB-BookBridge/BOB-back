package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record CreateChatRoomCommand(
    Long postId,
    Long tradeId,
    UUID buyerId,
    boolean isFar
) {

}