package com.bob.web.trade.adapter.in;

import com.bob.domain.chat.service.port.out.ChatTradePort;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatTradeAdapter implements ChatTradePort {

  private final TradeWriteUseCase writeUseCase;
  private final TradeReadUseCase readUseCase;

  @Override
  public Long createTrade(Long postId, UUID sellerId, UUID buyerId) {
    return writeUseCase.createTradeProcess(CreateTradeCommand.of(postId, sellerId, buyerId));
  }

  @Override
  public TradeDetailResponse readChatTradeSummary(Long tradeId, UUID memberId) {
    return readUseCase.readTradeDetailProcess(ReadTradeDetailQuery.of(tradeId, memberId));
  }
}
