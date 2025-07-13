package com.bob.support.fixture.command;

import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import java.util.UUID;

public class CreateChatRoomCommandFixture {

  public static CreateChatRoomCommand DEFAULT_CREATE_CHAT_ROOM_COMMAND(UUID memberId) {
    return new CreateChatRoomCommand(1L, memberId, false);
  }

  public static CreateChatRoomCommand DEFAULT_CREATE_CHAT_ROOM_COMMAND_WITH_FAR(UUID memberId) {
    return new CreateChatRoomCommand(1L, memberId, true);
  }

  public static CreateChatRoomCommand of(Long postId, UUID sellerId) {
    return new CreateChatRoomCommand(postId, sellerId, false);
  }
}
