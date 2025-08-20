package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.TradeStatus.CANCELED;
import static com.bob.domain.trade.entity.status.TradeStatus.REQUESTED;
import static com.bob.domain.trade.entity.status.TradeStatus.valueOf;
import static com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary.from;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CHAT_CANCELED_WITH_REASON;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CHAT_DEFAULT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.NOTI_CANCELED_WITH_REASON;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.NOTI_DEFAULT;
import static com.bob.global.event.application.dto.type.NotiEventType.TRADE;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;
import static java.time.LocalDateTime.now;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.TradeStatus;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.TradePostSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import com.bob.global.event.application.dto.NotiEvent;
import com.bob.global.event.application.dto.SystemChatMessageEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TradeService implements TradeWriteUseCase, TradeReadUseCase, TradeModifyUseCase {

  private final TradeRepository tradeRepository;
  private final TradeReader tradeReader;

  private final TradeMemberPort memberPort;
  private final TradePostPort postPort;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public Long createTradeProcess(CreateTradeCommand command) {
    Trade trade = tradeRepository.save(command.toTrade());
    return trade.getId();
  }

  @Transactional(readOnly = true)
  public TradesResponse readTradesProcess(ReadTradesQuery query) {
    UUID ownerId = postPort.readTradePostSummary(query.postId()).sellerId();
    verifyTradeOwner(ownerId, query.memberId());
    return TradesResponse.of(tradeReader.readTradesByPostId(query.postId()).stream()
        .map(trade -> TradeSummary.from(trade, from(memberPort.readTradeMemberProfile(trade.getBuyerId()))))
        .toList());
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
      throw new ApplicationException(TRADE_ACCESS_DENIED);
    }
  }

  @Transactional
  public void changeTradeStatusProcess(ChangeTradeStatusCommand command) {
    Trade trade = tradeReader.readTradeById(command.tradeId());
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));
    verifyTradeOwner(post.sellerId(), command.memberId());
    verifyRequestedOnly(trade.getId(), trade.getPostId(), command.status());

    final TradeStatus status = valueOf(command.status());
    verifyIsSameRequest(trade.getTradeStatus(), status);
    trade.updateTradeStatus(status, now());
    postPort.changePostStatus(trade.getPostId(), status.toPostStatusValue());

    final String notificationBody = buildNotificationBody(post.title(), status, command.reason());
    final String chatMessageBody = buildChatMessageBody(status, command.reason());
    sendTradeNotification(post, trade.getSellerId(), trade.getBuyerId(), notificationBody);
    publishSystemMessageEvent(post, command.memberId(), trade.getBuyerId(), chatMessageBody);
  }

  private void sendTradeNotification(TradePostSummary post, UUID memberId, UUID buyerId, String body) {
    NotiEvent event = NotiEvent.toSystemNotiEvent(TRADE, String.valueOf(post.postId()), "SYSTEM", memberId, buyerId, body);
    eventPublisher.publishEvent(event);
    System.out.println("good1");
  }

  private void publishSystemMessageEvent(TradePostSummary post, UUID memberId, UUID buyerId, String body) {
    SystemChatMessageEvent event = SystemChatMessageEvent.of("TRADE", post.postId().toString(), memberId, buyerId, body);
    eventPublisher.publishEvent(event);
    System.out.println("good2");
  }

  private static void verifyTradeOwner(UUID ownerId, UUID memberId) {
    if (!ownerId.equals(memberId)) {
      throw new ApplicationException(TRADE_ACCESS_DENIED);
    }
  }

  private void verifyRequestedOnly(Long requestId, Long postId, String status) {
    if (valueOf(status) == CANCELED || valueOf(status) == REQUESTED) {
      return;
    }
    tradeReader.readTradesByPostId(postId).stream()
        .filter(t -> !t.getId().equals(requestId))
        .filter(t -> t.getTradeStatus().isProcessed())
        .findAny()
        .ifPresent(t -> {
          throw new ApplicationException(TRADE_ALREADY_PROCESSED);
        });
  }

  private static void verifyIsSameRequest(TradeStatus s1, TradeStatus s2) {
    if (s1 == s2) {
      throw new ApplicationException(TRADE_STATUS_UNCHANGED);
    }
  }

  private static String buildNotificationBody(String title, TradeStatus status, String reason) {
    if (status == CANCELED) {
      return normalizeReason(reason) != null
          ? NOTI_CANCELED_WITH_REASON.format(title, CANCELED.value(), normalizeReason(reason))
          : NOTI_DEFAULT.format(title, CANCELED);
    }
    return NOTI_DEFAULT.format(title, status);
  }

  private static String buildChatMessageBody(TradeStatus status, String reason) {
    if (status == CANCELED) {
      return normalizeReason(reason) != null
          ? CHAT_CANCELED_WITH_REASON.format(normalizeReason(reason))
          : CHAT_DEFAULT.format();
    }
    return CHAT_DEFAULT.format(status);
  }

  private static String normalizeReason(String reason) {
    if (reason == null || reason.trim().isEmpty()) {
      return null;
    }
    return reason.trim();
  }
}
