package com.bob.domain.trade.service;

import static com.bob.domain.trade.service.dto.response.TradesResponse.of;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TradeService implements TradeWriteUseCase, TradeReadUseCase {

  private final TradeRepository tradeRepository;
  private final TradeReader tradeReader;

  private final TradeMemberPort memberPort;
  private final TradePostPort postPort;

  @Transactional
  public Long createTradeProcess(CreateTradeCommand command) {
    Trade trade = tradeRepository.save(command.toTrade());
    return trade.getId();
  }

  @Transactional(readOnly = true)
  public TradesResponse readTradesProcess(ReadTradesQuery query) {
    UUID ownerId = postPort.readTradePostOwnerId(query.postId());
    verifyTradeOwner(ownerId, query.memberId());
    return of(tradeReader.readTradesByPostId(query.postId()).stream().map(trade -> TradeSummary
        .from(trade, TradeMemberSummary.from(memberPort.readTradeMemberProfile(trade.getBuyerId())))
    ).toList());
  }

  private void verifyTradeOwner(UUID ownerId, UUID memberId) {
    if (!ownerId.equals(memberId)) {
      throw new ApplicationException(ApplicationError.TRADE_ACCESS_DENIED);
    }
  }

  @Transactional(readOnly = true)
  public TradeDetailResponse readTradeDetailProcess(ReadTradeDetailQuery query) {
    Trade trade = tradeReader.readTradeById(query.tradeId());
    verifyTradeParticipate(trade, query.memberId());
    return TradeDetailResponse.from(trade);
  }

  private void verifyTradeParticipate(Trade trade, UUID memberId) {
    UUID sellerId = trade.getSellerId();
    UUID buyerId = trade.getBuyerId();

    if (!memberId.equals(sellerId) && !memberId.equals(buyerId)) {
      throw new ApplicationException(ApplicationError.TRADE_ACCESS_DENIED);
    }
  }
}
