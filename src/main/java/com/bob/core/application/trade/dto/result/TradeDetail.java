package com.bob.core.application.trade.dto.result;

import java.util.List;
import java.util.UUID;

import com.bob.core.application.trade.dto.result.internal.TradeItemSummary;
import com.bob.core.application.trade.port.result.TradeMember;
import com.bob.core.application.trade.port.result.TradePost;

public record TradeDetail(Long id, String status, Post post, Trader seller, Trader buyer) {

    public static TradeDetail of(Long id, String status, Post post, Trader seller, Trader buyer) {
        return new TradeDetail(id, status, post, seller, buyer);
    }

    public record Post(Long id, String title, String cover) {

        public static Post from(TradePost post) {
            return new Post(post.id(), post.title(), post.thumbnailUrl());
        }
    }

    public record Trader(UUID id, String nickname, Integer worth, List<TradeItemSummary> item) {

        public static Trader from(TradeMember member, int itemsWorth, List<TradeItemSummary> items) {
            return new Trader(member.id(), member.nickname(), itemsWorth, items);
        }
    }
}
