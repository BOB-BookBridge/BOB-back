package com.bob.integration.adapter.trade;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.dto.result.MemberWishDetail;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.trade.port.out.TradeMemberPort;
import com.bob.core.application.trade.port.result.TradeBookcaseItem;
import com.bob.core.application.trade.port.result.TradeMember;

@Component
@RequiredArgsConstructor
public class TradeMemberAdapter implements TradeMemberPort {

    private final MemberReader memberReader;

    private final TradeBookcaseAdapter bookcaseAdapter;

    @Override
    public TradeMember readTradeMemberProfile(UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        return new TradeMember(detail.id(), detail.nickname(), detail.profileImageUrl());
    }

    @Override
    public boolean wishAllMatch(UUID memberId, List<Long> ids) {
        List<Long> wishBookIds = getWishBookIds(memberId);
        List<Long> requesterBookIds = getRequesterBookIds(ids);

        return new HashSet<>(wishBookIds).containsAll(requesterBookIds);
    }

    private List<Long> getWishBookIds(UUID memberId) {
        MemberDetail result = memberReader.readDetail(memberId, false);
        return result.wishes().stream().map(MemberWishDetail::bookId).toList();
    }

    private List<Long> getRequesterBookIds(List<Long> ids) {
        List<TradeBookcaseItem> result = bookcaseAdapter.read(ids);
        return result.stream().map(TradeBookcaseItem::bookId).toList();
    }
}
