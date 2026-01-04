package com.bob.integration.adapter.chat;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.chat.application.port.out.ChatTradePort;
import com.bob.core.trade.application.port.in.TradeReader;

@Component
@RequiredArgsConstructor
public class ChatTradeAdapter implements ChatTradePort {

    private final TradeReader tradeReader;

    @Override
    public String readTradeStatus(Long tradeId) {
        return tradeReader.readTradeStatus(tradeId);
    }
}
