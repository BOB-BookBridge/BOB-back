package com.bob.web.chat.adapter;

import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import com.bob.domain.trade.service.port.out.TradeChatPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradeChatAdapter implements TradeChatPort {

  private final ChatRoomWriteUseCase writeUseCase;

  @Override
  public Long create(Long postId, Long tradeId, UUID buyerId, boolean isFar) {
    CreateChatRoomCommand command = CreateChatRoomCommand.of(postId, tradeId, buyerId, isFar);
    CreateChatRoomResponse response = writeUseCase.createChatRoomProcess(command);
    return response.chatRoomId();
  }
}
