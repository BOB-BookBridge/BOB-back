package com.bob.domain.chat.usecase;

import com.bob.domain.chat.service.dto.command.ExitChatRoomCommand;

public interface ChatRoomModifyUseCase {

  void exitChatRoomProcess(ExitChatRoomCommand command);
}