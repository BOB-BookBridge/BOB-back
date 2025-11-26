package com.bob.core.application.chat.port.in;

import java.util.List;
import java.util.Optional;

import com.bob.core.application.chat.dto.query.ReadChatroomByPostAndMemberQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomDetailQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.application.chat.dto.query.ValidateParticipantQuery;
import com.bob.core.application.chat.dto.result.ChatroomDetail;
import com.bob.core.application.chat.dto.result.ChatroomSummary;
import com.bob.core.domain.chat.Chatroom;

public interface ChatroomReader {

    Chatroom read(Long chatroomId);

    Optional<Chatroom> readByPostAndBuyer(ReadChatroomByPostAndMemberQuery query);

    List<ChatroomSummary> readChatRoomSummaries(ReadChatroomSummariesQuery query);

    ChatroomDetail readChatRoomDetail(Long chatroomId, ReadChatroomDetailQuery query);

    void validateParticipant(Long chatroomId, ValidateParticipantQuery query);
}
