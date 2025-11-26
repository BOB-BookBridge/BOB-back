package com.bob.core.application.chat.dto.command;

import java.util.UUID;

public record CreateChatroomCommand(Long postId, Long tradeId, UUID buyerId, boolean isFar) {

}
