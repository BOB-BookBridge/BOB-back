package com.bob.core.trade.application.dto.result.internal;

import java.util.UUID;

import com.bob.core.trade.application.port.result.TradeBookcaseItem;
import com.bob.core.trade.application.port.result.TradeMember;

public record TradeMemberDetail(UUID id, String nickname, Item item) {

    public static TradeMemberDetail of(TradeMember member, TradeBookcaseItem item, int itemSize) {
        return new TradeMemberDetail(member.id(), member.nickname(), new Item(item.title(), item.cover(), itemSize));
    }

    public record Item(String title, String cover, int size) {

    }
}
