package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.Status.ACCEPTED;
import static com.bob.domain.trade.entity.status.Status.CANCELED;
import static com.bob.domain.trade.entity.status.Status.COMPLETED;
import static com.bob.domain.trade.entity.status.Status.REQUESTED;
import static com.bob.domain.trade.entity.status.Status.RESERVED;
import static com.bob.domain.trade.entity.status.Status.valueOf;
import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;
import static com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary.from;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CANCELED_WITH_REASON_CHAT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CANCELED_WITH_REASON_NOTI;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.CHANGED_TRADE_ITEM_CHAT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.REQUESTED_NOTI;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.STATUS_CHANGED_CHAT;
import static com.bob.domain.trade.service.util.TradeMessageTemplate.STATUS_CHANGED_NOTI;
import static com.bob.global.event.application.dto.type.NotiEventType.TRADE;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_TRADE_MEMBER;
import static com.bob.global.exception.response.ApplicationError.MAIN_TRADE_ITEM_CONTAINED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_COMPLETED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_POST_REMOVED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_NOT_CHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;
import static com.bob.global.exception.response.ApplicationError.UNCHANGEABLE_TRADE_ITEM;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toUnmodifiableMap;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.dto.query.ReadParticipateTradeStatusQuery;
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradeStatusMapResult;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.PostTradeSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeItemSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary;
import com.bob.domain.trade.service.dto.response.internal.TradePostSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.domain.trade.service.port.out.TradeChatPort;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.port.view.TradeItemView;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import com.bob.global.event.application.dto.NotiEvent;
import com.bob.global.event.application.dto.SystemChatMessageEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
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

  private final TradeItemService tradeItemService;

  private final TradeMemberPort memberPort;
  private final TradePostPort postPort;
  private final TradeChatPort chatPort;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public CreateTradeResponse createTradeProcess(CreateTradeCommand command) {
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(command.postId()));
    verifyBuyer(post.sellerId(), command.buyerId());
    verifyTradePostAccessible(post.status());

    return tradeRepository.findIdByPostIdAndBuyerId(post.id(), command.buyerId())
        .map(CreateTradeResponse::of)
        .orElseGet(() -> {
          Trade trade = tradeRepository.save(Trade.create(command.postId(), post.sellerId(), command.buyerId(), command.isFar()));
          registerTradeItem(command, trade.getId(), post.sellerBookId());

          TradeMemberSummary buyer = TradeMemberSummary.from(memberPort.readTradeMemberProfile(command.buyerId()));
          final String notificationBody = REQUESTED_NOTI.format(buyer.nickname(), post.title());
          sendTradeNotification(post, command.buyerId(), post.sellerId(), notificationBody);

          return CreateTradeResponse.of(trade.getId());
        });
  }

  private void registerTradeItem(CreateTradeCommand command, Long tradeId, Long sellerItemId) {
    memberPort.changeMemberBookUsage(command.buyerId(), command.postId(), command.itemIds(), false);
    tradeItemService.createTradeItemsProcess(CreateTradeItemsCommand.of(tradeId, List.of(sellerItemId), SELLER));
    tradeItemService.createTradeItemsProcess(CreateTradeItemsCommand.of(tradeId, command.itemIds(), BUYER));
  }

  private void verifyBuyer(UUID sellerId, UUID buyerId) {
    if (Objects.equals(sellerId, buyerId))
      throw new ApplicationException(IS_SAME_TRADE_MEMBER);
  }

  @Transactional(readOnly = true)
  public PostTradesResponse readPostTradesProcess(ReadPostTradesQuery query) {
    UUID ownerId = postPort.readTradePostSummary(query.postId()).sellerId();
    verifyTradeOwner(ownerId, query.memberId(), null);
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

    Map<Owner, List<Long>> tradeItemOwnerMap = tradeItemService.readTradeItemsOwnerMapProcess(query.tradeId());
    List<Long> sellerItemIds = tradeItemOwnerMap.get(SELLER);
    List<Long> buyerItemIds = tradeItemOwnerMap.get(BUYER);
    List<Long> allItemIds = Stream.concat(sellerItemIds.stream(), buyerItemIds.stream()).toList();

    List<TradeItemView> bookRes = memberPort.readTradeItemSummary(allItemIds);
    List<TradeItemSummary> sellerItems = TradeItemSummary.listFrom(bookRes, sellerItemIds);
    List<TradeItemSummary> buyerItems = TradeItemSummary.listFrom(bookRes, buyerItemIds);
    int sellerItemsWorth = sellerItems.stream().map(TradeItemSummary::priceStandard).mapToInt(Integer::intValue).sum();
    int buyerItemsWorth = buyerItems.stream().map(TradeItemSummary::priceStandard).mapToInt(Integer::intValue).sum();

    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));
    TradeMemberSummary seller = TradeMemberSummary.from(memberPort.readTradeMemberProfile(trade.getSellerId()));
    TradeMemberSummary buyer = TradeMemberSummary.from(memberPort.readTradeMemberProfile(trade.getBuyerId()));

    return TradeDetailResponse.from(
        trade.getId(), trade.getStatus().name(),
        TradeDetailResponse.Post.from(post),
        TradeDetailResponse.Trader.from(seller, sellerItemsWorth, sellerItems),
        TradeDetailResponse.Trader.from(buyer, buyerItemsWorth, buyerItems)
    );
  }

  @Transactional(readOnly = true)
  public TradeStatusMapResult readTradeStatusProcess(ReadParticipateTradeStatusQuery query) {
    List<Trade> trades = tradeReader.readTradesByBuyerIdAndPostId(query.memberId(), query.postIds());
    Map<Long, String> map = trades.stream().collect(toUnmodifiableMap(Trade::getPostId, t -> t.getStatus().name()));
    return TradeStatusMapResult.of(map);
  }

  @Transactional
  public ChangeTradeStatusResult changeTradeStatusProcess(ChangeTradeStatusCommand command) {
    Trade trade = tradeReader.readTradeById(command.tradeId());
    Status status = valueOf(command.status());
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));

    verifyTradeParticipate(trade, command.memberId());
    verifyTradeOwner(post.sellerId(), command.memberId(), status);
    verifyTradeChangeable(trade.getStatus(), status);
    verifyPostRequested(trade.getId(), trade.getPostId(), command.status());
    Long chatroomId = changeStatus(trade, status, post);

    String notificationBody = buildChangeStatusNotificationBody(post.title(), status, command.reason());
    UUID senderId = command.memberId();
    UUID receiverId = Objects.equals(senderId, trade.getSellerId()) ? trade.getBuyerId() : trade.getSellerId();
    sendTradeNotification(post, senderId, receiverId, notificationBody);

    if(status.isProcessed() || status.isAborted()) {
      String chatMessageBody = buildChangeStatusChatMessageBody(status, command.reason());
      publishSystemMessageEvent(post, senderId, receiverId, chatMessageBody);
    }
    return ChangeTradeStatusResult.of(chatroomId);
  }

  private Long changeStatus(Trade trade, Status status, TradePostSummary post) {
    return switch (status) {
      case CANCELED, REJECTED -> onAborted(trade, status, post);
      case ACCEPTED -> onAccepted(trade);
      case RESERVED -> onReserved(trade);
      case COMPLETED -> onCompleted(trade, post);
      default -> null;
    };
  }

  private Long onAborted(Trade trade, Status status, TradePostSummary post) {
    changePostTradeProgressIfReserved(trade);
    trade.updateTradeStatus(status, now());
    tradeItemService.freeTraderItemsExcludeMainItem(trade.getId(), post.sellerBookId());
    return null;
  }

  private Long onAccepted(Trade trade) {
    changePostTradeProgressIfReserved(trade);
    trade.updateTradeStatus(ACCEPTED, now());
    return chatPort.create(trade.getPostId(), trade.getId(), trade.getBuyerId(), trade.isFar());
  }

  private Long onReserved(Trade trade) {
    trade.updateTradeStatus(RESERVED, now());
    postPort.changeTradeProgress(trade.getPostId(), RESERVED.toPostStatusValue());
    return null;
  }

  private Long onCompleted(Trade trade, TradePostSummary post) {
    trade.updateTradeStatus(COMPLETED, now());
    tradeRepository.cancelOtherTrades(trade.getPostId(), trade.getId());
    tradeItemService.freeOtherTraderItems(trade.getPostId(), trade.getId(), post.sellerBookId());
    tradeItemService.freeTraderItemsExcludeMainItem(trade.getId(), post.sellerBookId());
    tradeItemService.removeTraderItems(trade.getId());
    postPort.changeTradeProgress(trade.getPostId(), COMPLETED.toPostStatusValue());
    return null;
  }

  private static void verifyTradeChangeable(Status current, Status request) {
    if (current == request)
      throw new ApplicationException(TRADE_STATUS_UNCHANGED);

    if (current == COMPLETED)
      throw new ApplicationException(TRADE_ALREADY_COMPLETED);

    if (request == REQUESTED)
      throw new ApplicationException(TRADE_STATUS_NOT_CHANGEABLE);

    if (request == ACCEPTED && current.isAborted())
      throw new ApplicationException(TRADE_ALREADY_ABORTED);
  }

  private void changePostTradeProgressIfReserved(Trade trade) {
    if (trade.getStatus() == RESERVED)
      postPort.changeTradeProgress(trade.getPostId(), REQUESTED.toPostStatusValue());
  }

  @Transactional
  public void changeTradeItemProcess(ChangeTradeItemsCommand command) {
    Trade trade = tradeReader.readTradeById(command.tradeId());
    TradePostSummary post = TradePostSummary.from(postPort.readTradePostSummary(trade.getPostId()));
    verifyTradeParticipate(trade, command.memberId());
    verifyTradePostAccessible(post.status());
    verifyTradeItemChangeable(trade.getStatus());
    verifyTradeMainItemContains(command.itemIds(), post.sellerBookId());

    Owner owner = trade.getBuyerId().equals(command.memberId()) ? BUYER : SELLER;
    if (trade.getStatus().isAborted())
      trade.updateTradeStatus(REQUESTED, now());
    tradeItemService.changeTradeItemsProcess(command, post.id(), owner);

    final String messageBody = CHANGED_TRADE_ITEM_CHAT.format();
    UUID receiverId = owner == SELLER ? trade.getBuyerId() : trade.getSellerId();
    publishSystemMessageEvent(post, command.memberId(), receiverId, messageBody);
  }

  private void verifyTradeParticipate(Trade trade, UUID memberId) {
    UUID sellerId = trade.getSellerId();
    UUID buyerId = trade.getBuyerId();

    if (!memberId.equals(sellerId) && !memberId.equals(buyerId))
      throw new ApplicationException(TRADE_ACCESS_DENIED);
  }

  private void verifyTradePostAccessible(String postStatus) {
    if (Objects.equals(postStatus, "REMOVED"))
      throw new ApplicationException(TRADE_POST_REMOVED);
  }

  private void verifyTradeItemChangeable(Status status) {
    if (status.isProcessed())
      throw new ApplicationException(UNCHANGEABLE_TRADE_ITEM);
  }

  private void verifyTradeMainItemContains(List<Long> itemIds, Long mainItemId) {
    if (itemIds.contains(mainItemId))
      throw new ApplicationException(MAIN_TRADE_ITEM_CONTAINED);
  }

  private static void verifyTradeOwner(UUID ownerId, UUID requesterId, Status status) {
    if (status != CANCELED && !ownerId.equals(requesterId))
      throw new ApplicationException(TRADE_ACCESS_DENIED);
  }

  private void verifyPostRequested(Long requestId, Long postId, String status) {
    if (!valueOf(status).isProcessed())
      return;

    tradeReader.readTradesByPostId(postId).stream()
        .filter(t -> !t.getId().equals(requestId))
        .filter(t -> t.getStatus().isProcessed())
        .findAny()
        .ifPresent(t -> { throw new ApplicationException(TRADE_ALREADY_PROCESSED); });
  }

  private void sendTradeNotification(TradePostSummary post, UUID senderId, UUID receiverId, String body) {
    NotiEvent event = NotiEvent.toSystemNotiEvent(TRADE, String.valueOf(post.id()), "SYSTEM", senderId, receiverId, body);
    eventPublisher.publishEvent(event);
  }

  private void publishSystemMessageEvent(TradePostSummary post, UUID senderId, UUID receiverId, String body) {
    SystemChatMessageEvent event = SystemChatMessageEvent.of("TRADE", post.id().toString(), senderId, receiverId, body);
    eventPublisher.publishEvent(event);
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
