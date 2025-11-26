package com.bob.core.application.chat.port.in;

import com.bob.core.application.chat.dto.command.CreateChatroomCommand;
import com.bob.core.domain.chat.Chatroom;

public interface ChatroomCreator {

    Chatroom create(CreateChatroomCommand command);
}
