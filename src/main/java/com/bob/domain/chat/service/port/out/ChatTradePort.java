package com.bob.domain.chat.service.port.out;

import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import java.util.UUID;

public interface ChatTradePort {

  Long createTrade(Long postId, UUID sellerId, UUID buyerId);

  TradeDetailResponse readChatTradeSummary(Long tradeId, UUID memberId);
}
