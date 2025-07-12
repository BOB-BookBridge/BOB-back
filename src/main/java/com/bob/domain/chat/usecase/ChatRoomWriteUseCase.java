package com.bob.domain.chat.usecase;

import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;

public interface ChatRoomWriteUseCase {

  CreateChatRoomResponse createChatRoomProcess(CreateChatRoomCommand command);

  void createChatRoomMessageProcess(CreateChatMessageCommand command);
}
