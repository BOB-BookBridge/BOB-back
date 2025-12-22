package com.bob.core.chat.application.port.in;

import com.bob.core.chat.application.dto.command.CreateMessageCommand;
import com.bob.core.chat.application.dto.command.CreateSystemMessageCommand;
import com.bob.core.chat.application.dto.result.ChatMessageCreationResult;
import com.bob.core.chat.domain.ChatMessage;

public interface ChatMessageCreator {

    ChatMessageCreationResult createChatMessage(Long chatroomId, CreateMessageCommand command);

    ChatMessage createSystemChatMessage(CreateSystemMessageCommand command);
}
