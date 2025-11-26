package com.bob.core.application.chat.port.in;

import java.util.List;

import com.bob.core.application.chat.dto.query.ReadChatMessagesQuery;
import com.bob.core.application.chat.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.application.chat.dto.result.ChatMessageSummary;

public interface ChatMessageReader {

    List<ChatMessageSummary> readMessages(Long chatroomId, ReadChatMessagesQuery query);

    int countUnreadMessagesByMember(ReadUnreadMessageCountQuery query);
}
