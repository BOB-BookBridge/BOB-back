package com.bob.domain.trade.service.port.out;

import com.bob.domain.post.service.dto.response.PostDetailResponse;

public interface TradePostPort {

  PostDetailResponse readTradePostSummary(Long postId);

  void changeTradeProgress(Long postId, String status);
}
