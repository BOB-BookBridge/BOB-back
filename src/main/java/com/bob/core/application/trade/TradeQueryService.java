package com.bob.core.application.trade;

import static com.bob.core.domain.trade.type.Owner.BUYER;
import static com.bob.core.domain.trade.type.Owner.SELLER;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.trade.dto.query.ReadPostTradesQuery;
import com.bob.core.application.trade.dto.query.ReadTradeDetailQuery;
import com.bob.core.application.trade.dto.query.ReadTradeStatusMapQuery;
import com.bob.core.application.trade.dto.query.ReadTradesQuery;
import com.bob.core.application.trade.dto.result.TradeDetail;
import com.bob.core.application.trade.dto.result.TradeSummaries;
import com.bob.core.application.trade.dto.result.TradeSummary;
import com.bob.core.application.trade.dto.result.internal.PostTrade;
import com.bob.core.application.trade.dto.result.internal.TradeItemSummary;
import com.bob.core.application.trade.dto.result.internal.TradeMemberDetail;
import com.bob.core.application.trade.port.in.TradeReader;
import com.bob.core.application.trade.port.out.TradeBookcasePort;
import com.bob.core.application.trade.port.out.TradeMemberPort;
import com.bob.core.application.trade.port.out.TradePostPort;
import com.bob.core.application.trade.port.result.TradeBookcaseItem;
import com.bob.core.application.trade.port.result.TradeMember;
import com.bob.core.application.trade.port.result.TradePost;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeQueryService implements TradeReader {

    private final TradeRepository tradeRepository;
    private final TradeMemberPort memberPort;
    private final TradeBookcasePort bookcasePort;
    private final TradePostPort postPort;

    @Override
    public Trade read(Long id) {
        return tradeRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_TRADE));
    }

    @Override
    public List<PostTrade> readPostTradeSummaries(ReadPostTradesQuery query) {
        UUID ownerId = postPort.read(query.postId()).sellerId();
        verifyTradeOwner(ownerId, query.memberId());

        return tradeRepository.findAllByPostId(query.postId()).stream()
            .map(trade -> PostTrade.of(trade, memberPort.readTradeMemberProfile(trade.getBuyerId())))
            .toList();
    }

    @Override
    public TradeSummaries readTrades(ReadTradesQuery query, Pageable pageable) {
        List<Trade> trades = tradeRepository.findTradesByQuery(query, pageable);
        Long size = tradeRepository.countTradesByQuery(query);

        Map<UUID, TradeMember> tradersMap = new HashMap<>();
        List<TradeSummary> result = trades.stream().map(trade -> {
            TradePost post = postPort.read(trade.getPostId());

            TradeMember seller = tradersMap.computeIfAbsent(post.sellerId(), memberPort::readTradeMemberProfile);
            TradeMember buyer = tradersMap.computeIfAbsent(trade.getBuyerId(), memberPort::readTradeMemberProfile);

            List<Long> sellerItemIds = trade.getItemIdsByOwner(SELLER);
            List<Long> buyerItemIds = trade.getItemIdsByOwner(BUYER);
            TradeBookcaseItem sellerMainItem = bookcasePort.read(post.sellerBookId());
            TradeBookcaseItem buyerMainItem = bookcasePort.read(buyerItemIds.get(0));

            return TradeSummary.of(
                trade.getId(),
                trade.getStatus(),
                TradeMemberDetail.of(seller, sellerMainItem, sellerItemIds.size()),
                TradeMemberDetail.of(buyer, buyerMainItem, buyerItemIds.size())
            );
        }).toList();
        return TradeSummaries.of(size, result);
    }

    @Override
    public TradeDetail readTradeDetail(Long id, ReadTradeDetailQuery query) {
        Trade trade = read(id);
        verifyTradeParticipate(trade, query.memberId());

        List<Long> sellerItemIds = trade.getItemIdsByOwner(SELLER);
        List<Long> buyerItemIds = trade.getItemIdsByOwner(BUYER);
        List<Long> allItemIds = Stream.concat(sellerItemIds.stream(), buyerItemIds.stream()).toList();

        List<TradeBookcaseItem> books = bookcasePort.read(allItemIds);

        List<TradeItemSummary> sellerItems = TradeItemSummary.listFrom(books, sellerItemIds);
        int sellerItemsWorth = sellerItems.stream()
            .map(TradeItemSummary::priceStandard)
            .mapToInt(Integer::intValue)
            .sum();

        List<TradeItemSummary> buyerItems = TradeItemSummary.listFrom(books, buyerItemIds);
        int buyerItemsWorth = buyerItems.stream()
            .map(TradeItemSummary::priceStandard)
            .mapToInt(Integer::intValue)
            .sum();

        TradePost post = postPort.read(trade.getPostId());
        TradeMember seller = memberPort.readTradeMemberProfile(trade.getSellerId());
        TradeMember buyer = memberPort.readTradeMemberProfile(trade.getBuyerId());

        return TradeDetail.of(
            trade.getId(), trade.getStatus().name(),
            TradeDetail.Post.from(post),
            TradeDetail.Trader.from(seller, sellerItemsWorth, sellerItems),
            TradeDetail.Trader.from(buyer, buyerItemsWorth, buyerItems)
        );
    }

    @Override
    public String readTradeStatus(Long id) {
        return tradeRepository.findById(id)
            .map(t -> t.getStatus().name())
            .orElse("REMOVED");
    }

    @Override
    public Map<Long, String> readTradeStatusMap(ReadTradeStatusMapQuery query) {
        List<Trade> trades = tradeRepository.findAllByBuyerIdAndPostIdIn(query.memberId(), query.postIds());

        return trades.stream()
            .collect(toUnmodifiableMap(Trade::getPostId, t -> t.getStatus().name()));
    }

    private void verifyTradeParticipate(Trade trade, UUID memberId) {
        if (!memberId.equals(trade.getSellerId()) && !memberId.equals(trade.getBuyerId()))
            throw new ApplicationException(TRADE_ACCESS_DENIED);
    }

    private void verifyTradeOwner(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId))
            throw new ApplicationException(TRADE_ACCESS_DENIED);
    }
}
