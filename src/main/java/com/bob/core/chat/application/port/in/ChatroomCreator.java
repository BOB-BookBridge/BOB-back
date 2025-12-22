package com.bob.core.chat.application.port.in;

import com.bob.core.chat.application.dto.command.CreateChatroomCommand;
import com.bob.core.chat.domain.Chatroom;

public interface ChatroomCreator {

    Chatroom create(CreateChatroomCommand command);
}
