package com.bob.domain.trade.adapter;

import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.trade.service.TradeService;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatTradeAdapter implements ChatTradePort {

  private final TradeService tradeService;

  @Override
  public Long createTrade(Long postId, UUID sellerId, UUID buyerId) {
    return tradeService.createTrade(CreateTradeCommand.of(postId, sellerId, buyerId));
  }
}
