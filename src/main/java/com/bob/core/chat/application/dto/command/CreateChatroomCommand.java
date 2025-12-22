package com.bob.core.chat.application.dto.command;

import java.util.UUID;

public record CreateChatroomCommand(Long postId, Long tradeId, UUID buyerId, boolean isFar) {

}
