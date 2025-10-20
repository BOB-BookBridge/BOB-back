package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record CreateChatRoomCommand(
    Long postId,
    Long tradeId,
    UUID buyerId,
    boolean isFar
) {

  public static CreateChatRoomCommand of(Long postId, Long tradeId, UUID buyerId, boolean isFar) {
    return new CreateChatRoomCommand(postId, tradeId, buyerId, isFar);
  }
}