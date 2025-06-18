package com.bob.web.trade.adapter.in;

import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatTradeAdapter implements ChatTradePort {

  private final TradeWriteUseCase writeUseCase;

  @Override
  public Long createTrade(Long postId, UUID sellerId, UUID buyerId) {
    return writeUseCase.createTradeProcess(CreateTradeCommand.of(postId, sellerId, buyerId));
  }
}
