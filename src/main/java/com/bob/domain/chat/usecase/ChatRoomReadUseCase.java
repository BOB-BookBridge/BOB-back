package com.bob.domain.chat.usecase;

import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import java.util.List;

public interface ChatRoomReadUseCase {

  List<ChatRoomSummaryResponse> readChatRoomListProcess(ReadChatRoomListQuery query);
}
