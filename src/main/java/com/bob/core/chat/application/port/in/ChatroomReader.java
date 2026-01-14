package com.bob.core.chat.application.port.in;

import java.util.List;
import java.util.Optional;

import com.bob.core.chat.application.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.chat.application.dto.query.ReadChatroomDetailQuery;
import com.bob.core.chat.application.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.chat.application.dto.query.ValidateParticipantQuery;
import com.bob.core.chat.application.dto.result.ChatroomDetail;
import com.bob.core.chat.application.dto.result.ChatroomSummary;
import com.bob.core.chat.domain.Chatroom;

public interface ChatroomReader {

    Chatroom read(Long chatroomId);

    Chatroom readByMessageId(Long messageId);

    Optional<Chatroom> readByPostAndBuyer(ReadChatroomByPostAndMemberQuery query);

    List<ChatroomSummary> readChatRoomSummaries(ReadChatroomSummariesQuery query);

    ChatroomDetail readChatRoomDetail(Long chatroomId, ReadChatroomDetailQuery query);

    void validateParticipant(Long chatroomId, ValidateParticipantQuery query);
}
