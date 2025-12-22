package com.bob.core.chat.application.port.in;

import java.util.List;

import com.bob.core.chat.application.dto.query.ReadChatMessagesQuery;
import com.bob.core.chat.application.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.chat.application.dto.result.ChatMessageSummary;

public interface ChatMessageReader {

    List<ChatMessageSummary> readMessages(Long chatroomId, ReadChatMessagesQuery query);

    int countUnreadMessagesByMember(ReadUnreadMessageCountQuery query);
}
