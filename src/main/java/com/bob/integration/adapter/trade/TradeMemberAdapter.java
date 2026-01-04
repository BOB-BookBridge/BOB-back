package com.bob.integration.adapter.trade;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.MemberWish;
import com.bob.core.trade.application.port.out.TradeMemberPort;
import com.bob.core.trade.application.port.result.TradeBookcaseItem;
import com.bob.core.trade.application.port.result.TradeMember;

@Component
@RequiredArgsConstructor
public class TradeMemberAdapter implements TradeMemberPort {

    private final MemberReader memberReader;

    private final TradeBookcaseAdapter bookcaseAdapter;

    @Override
    public TradeMember readTradeMemberProfile(UUID memberId) {
        MemberBasicInfo info = memberReader.readBasicInfo(memberId);

        return new TradeMember(info.id(), info.nickname(), info.profileImageUrl());
    }

    @Override
    public boolean wishAllMatch(UUID memberId, List<Long> ids) {
        Member member = memberReader.read(memberId);

        List<Long> wishBookIds = member.getWishes().stream().map(MemberWish::getBookId).toList();
        List<Long> requesterBookIds = getRequesterBookIds(ids);

        return new HashSet<>(wishBookIds).containsAll(requesterBookIds);
    }

    private List<Long> getRequesterBookIds(List<Long> ids) {
        List<TradeBookcaseItem> result = bookcaseAdapter.read(ids);
        return result.stream().map(TradeBookcaseItem::bookId).toList();
    }
}
