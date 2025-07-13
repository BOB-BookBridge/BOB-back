package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import java.util.List;
import java.util.UUID;

public class CreateChatMessageCommandFixture {

  public static CreateChatMessageCommand DEFAULT_CREATE_CHAT_MESSAGE_COMMAND() {
    return new CreateChatMessageCommand(1L, MEMBER_ID, "message", List.of());
  }

  public static CreateChatMessageCommand WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND() {
    return new CreateChatMessageCommand(1L, MEMBER_ID, "message", List.of("/chat/test.png"));
  }

  public static CreateChatMessageCommand CUSTOM_CREATE_CHAT_MESSAGE_COMMAND(UUID senderId) {
    return new CreateChatMessageCommand(1L, senderId, "message", List.of());
  }

  public static CreateChatMessageCommand CUSTOM_WITH_IMAGE_CREATE_CHAT_MESSAGE_COMMAND(Long chatRoomId, UUID senderId) {
    return new CreateChatMessageCommand(chatRoomId, senderId, "message", List.of("/chat/test.png"));
  }
}
