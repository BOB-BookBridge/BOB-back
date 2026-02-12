package com.bob.core.chat.application.port.in;

import com.bob.core.chat.application.dto.command.DeactivateChatroomCommand;
import com.bob.core.chat.application.dto.command.ExitChatroomCommand;
import com.bob.core.chat.application.dto.command.JoinChatroomCommand;

public interface ChatroomModifier {

    void join(Long chatroomId, JoinChatroomCommand command);

    void exit(Long chatroomId, ExitChatroomCommand command);

    void deactivate(DeactivateChatroomCommand command);
}
