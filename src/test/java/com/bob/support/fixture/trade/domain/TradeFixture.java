package com.bob.support.fixture.trade.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.status.Status;

public class TradeFixture {

    public static Trade createTrade(Long postId, UUID sellerId, UUID buyerId, Status status) {
        return Trade.builder()
            .postId(postId)
            .sellerId(sellerId)
            .buyerId(buyerId)
            .isFar(false)
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static Trade createTrade(Long postId, Status status) {
        return createTrade(postId, MEMBER_ID, OTHER_MEMBER_ID, status);
    }

    public static Trade createTrade(Status status) {
        return createTrade(1L, status);
    }
}
