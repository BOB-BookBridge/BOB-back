package com.bob.core.application.chat.port.in;

import com.bob.core.application.chat.dto.command.ExitChatroomCommand;
import com.bob.core.application.chat.dto.command.JoinChatroomCommand;

public interface ChatroomModifier {

    void join(Long chatroomId, JoinChatroomCommand command);

    void exit(Long chatroomId, ExitChatroomCommand command);
}
