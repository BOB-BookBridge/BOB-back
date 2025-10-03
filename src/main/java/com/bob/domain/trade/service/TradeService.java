package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.Status.CANCELED;
import static com.bob.domain.trade.entity.status.Status.REQUESTED;
import static com.bob.domain.trade.entity.status.Status.valueOf;
import static com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary.from;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CANCELED_WITH_REASON_CHAT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CANCELED_WITH_REASON_NOTI;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.REQUESTED_NOTI;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.STATUS_CHANGED_CHAT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.STATUS_CHANGED_NOTI;
import static com.bob.global.event.application.dto.type.NotiEventType.TRADE;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_TRADE_MEMBER;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;
import static java.time.LocalDateTime.now;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.PostTradeSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
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
  public CreateTradeResponse createTradeProcess(CreateTradeCommand command) {
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(command.postId()));
    verifyBuyer(post.sellerId(), command.buyerId());
    Trade trade = tradeRepository.save(Trade.of(command.postId(), post.sellerId(), command.buyerId()));
    TradeMemberSummary buyer = TradeMemberSummary.from(memberPort.readTradeMemberProfile(command.buyerId()));
    memberPort.changeMemberBookUsage(command.postId(), command.exchangeBookIds());
    final String notificationBody = REQUESTED_NOTI.format(buyer.nickname(), post.title());
    sendTradeNotification(post, command.buyerId(), post.sellerId(), notificationBody);
    return CreateTradeResponse.of(trade.getId());
  }

  private void verifyBuyer(UUID sellerId, UUID buyerId) {
    if (Objects.equals(sellerId, buyerId)) {
      throw new ApplicationException(IS_SAME_TRADE_MEMBER);
    }
  }

  @Transactional(readOnly = true)
  public PostTradesResponse readPostTradesProcess(ReadPostTradesQuery query) {
    UUID ownerId = postPort.readTradePostSummary(query.postId()).sellerId();
    verifyTradeOwner(ownerId, query.memberId());
    // TODO : 배치 조회 변경 필요
    return PostTradesResponse.of(tradeReader.readTradesByPostId(query.postId()).stream()
        .map(trade -> PostTradeSummary.from(trade, from(memberPort.readTradeMemberProfile(trade.getBuyerId()))))
        .toList());
  }

  @Transactional(readOnly = true)
  public TradesResponse readTradesProcess(ReadTradesQuery query, Pageable pageable) {
    List<Trade> trades = tradeReader.readTradesByQuery(query, pageable);
    Long size = tradeRepository.countTradesByQuery(query);
    // TODO : 배치 조회 변경 필요
    return TradesResponse.from(trades.stream().map(trade -> {
      TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));
      return TradeSummary.of(trade.getId(), trade.getStatus(), post);
    }).toList(), size);
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

    if (!memberId.equals(sellerId) && !memberId.equals(buyerId))
      throw new ApplicationException(TRADE_ACCESS_DENIED);
  }

  @Transactional
  public void changeTradeStatusProcess(ChangeTradeStatusCommand command) {
    Trade trade = tradeReader.readTradeById(command.tradeId());
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));
    verifyTradeOwner(post.sellerId(), command.memberId());
    verifyRequestedOnly(trade.getId(), trade.getPostId(), command.status());

    final Status status = valueOf(command.status());
    verifyIsSameRequest(trade.getStatus(), status);
    trade.updateTradeStatus(status, now());
    // TODO: 거래 완료 시 게시글에 관련된 모든 거래 CANCELED 로 변경
    postPort.changeTradeProgress(trade.getPostId(), status.toPostStatusValue());

    final String notificationBody = buildChangeStatusNotificationBody(post.title(), status, command.reason());
    final String chatMessageBody = buildChangeStatusChatMessageBody(status, command.reason());
    sendTradeNotification(post, trade.getSellerId(), trade.getBuyerId(), notificationBody);
    publishSystemMessageEvent(post, command.memberId(), trade.getBuyerId(), chatMessageBody);
  }

  private void sendTradeNotification(TradePostSummary post, UUID senderId, UUID receiverId, String body) {
    NotiEvent event = NotiEvent.toSystemNotiEvent(TRADE, String.valueOf(post.id()), "SYSTEM", senderId, receiverId, body);
    eventPublisher.publishEvent(event);
  }

  private void publishSystemMessageEvent(TradePostSummary post, UUID senderId, UUID receiverId, String body) {
    SystemChatMessageEvent event = SystemChatMessageEvent.of("TRADE", post.id().toString(), senderId, receiverId, body);
    eventPublisher.publishEvent(event);
  }

  private static void verifyTradeOwner(UUID ownerId, UUID memberId) {
    if (!ownerId.equals(memberId))
      throw new ApplicationException(TRADE_ACCESS_DENIED);
  }

  private void verifyRequestedOnly(Long requestId, Long postId, String status) {
    if (valueOf(status) == CANCELED || valueOf(status) == REQUESTED)
      return;
    tradeReader.readTradesByPostId(postId).stream()
        .filter(t -> !t.getId().equals(requestId))
        .filter(t -> t.getStatus().isProcessed())
        .findAny()
        .ifPresent(t -> {
          throw new ApplicationException(TRADE_ALREADY_PROCESSED);
        });
  }

  private static void verifyIsSameRequest(Status s1, Status s2) {
    if (s1 == s2)
      throw new ApplicationException(TRADE_STATUS_UNCHANGED);
  }

  private static String buildChangeStatusNotificationBody(String title, Status status, String reason) {
    if (status == CANCELED)
      return normalizeReason(reason) != null
          ? CANCELED_WITH_REASON_NOTI.format(title, CANCELED.value(), normalizeReason(reason))
          : STATUS_CHANGED_NOTI.format(title, CANCELED.value());
    return STATUS_CHANGED_NOTI.format(title, status.value());
  }

  private static String buildChangeStatusChatMessageBody(Status status, String reason) {
    if (status == CANCELED)
      return normalizeReason(reason) != null
          ? CANCELED_WITH_REASON_CHAT.format(normalizeReason(reason))
          : STATUS_CHANGED_CHAT.format(CANCELED.value());
    return STATUS_CHANGED_CHAT.format(status.value());
  }
  private static String normalizeReason(String reason) {
    if (reason == null || reason.trim().isEmpty())
      return null;
    return reason.trim();
  }
}
