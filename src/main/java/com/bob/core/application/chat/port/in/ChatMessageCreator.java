package com.bob.core.application.chat.port.in;

import com.bob.core.application.chat.dto.command.CreateMessageCommand;
import com.bob.core.application.chat.dto.command.CreateSystemMessageCommand;
import com.bob.core.domain.chat.ChatMessage;

public interface ChatMessageCreator {

    ChatMessage createChatMessage(Long chatroomId, CreateMessageCommand command);

    ChatMessage createSystemChatMessage(CreateSystemMessageCommand command);
}
