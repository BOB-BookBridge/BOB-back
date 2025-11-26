package com.bob.core.application.trade.port.out;

import com.bob.core.application.trade.port.result.TradePost;

public interface TradePostPort {

    TradePost read(Long postId);

    void changeTradeProgress(Long postId, String status);
}
