package com.bob.core.trade.application.port.out;

import com.bob.core.trade.application.port.result.TradePost;

public interface TradePostPort {

    TradePost read(Long postId);

    void changeTradeProgress(Long postId, String status);
}
