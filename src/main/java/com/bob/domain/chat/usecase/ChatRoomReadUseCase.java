package com.bob.domain.chat.usecase;

import com.bob.domain.chat.service.dto.query.ReadChatRoomDetailQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.query.ReadUnreadMessageCountQuery;
import com.bob.domain.chat.service.dto.query.ValidateParticipantQuery;
import com.bob.domain.chat.service.dto.response.ChatRoomDetailResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import java.util.List;

public interface ChatRoomReadUseCase {

  List<ChatRoomSummaryResponse> readChatRoomListProcess(ReadChatRoomListQuery query);

  int countUnreadMessageProcess(ReadUnreadMessageCountQuery query);

  ChatRoomDetailResponse readChatRoomDetailProcess(ReadChatRoomDetailQuery query);

  void validateParticipant(ValidateParticipantQuery query);
}
