package com.bob.core.application.trade;

import static com.bob.core.application.trade.util.TradeMessageTemplate.CANCELED_WITH_REASON_CHAT;
import static com.bob.core.application.trade.util.TradeMessageTemplate.CANCELED_WITH_REASON_NOTI;
import static com.bob.core.application.trade.util.TradeMessageTemplate.CHANGED_TRADE_ITEM_CHAT;
import static com.bob.core.application.trade.util.TradeMessageTemplate.REQUESTED_NOTI;
import static com.bob.core.application.trade.util.TradeMessageTemplate.STATUS_CHANGED_CHAT;
import static com.bob.core.application.trade.util.TradeMessageTemplate.STATUS_CHANGED_NOTI;
import static com.bob.core.domain.trade.Trade.createTrade;
import static com.bob.core.domain.trade.status.Status.ACCEPTED;
import static com.bob.core.domain.trade.status.Status.CANCELED;
import static com.bob.core.domain.trade.status.Status.COMPLETED;
import static com.bob.core.domain.trade.status.Status.REQUESTED;
import static com.bob.core.domain.trade.status.Status.RESERVED;
import static com.bob.core.domain.trade.status.Status.valueOf;
import static com.bob.core.domain.trade.type.Owner.BUYER;
import static com.bob.core.domain.trade.type.Owner.SELLER;
import static com.bob.global.event.application.dto.NotificationEvent.toSystemEvent;
import static com.bob.global.event.application.dto.type.NotiEventType.TRADE;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_TRADE_MEMBER;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_COMPLETED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_MAIN_ITEM_NOT_CONTAINED;
import static com.bob.global.exception.response.ApplicationError.TRADE_POST_REMOVED;
import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_DENIED_BY_REQUESTER;
import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_DENIED_BY_STATUS;
import static com.bob.global.exception.response.ApplicationError.TRADE_SELLER_WISH_NOT_MATCH;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_NOT_CHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;
import static com.bob.global.exception.response.ApplicationError.UNCHANGEABLE_TRADE_ITEM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.trade.dto.command.ChangeTradeItemsCommand;
import com.bob.core.application.trade.dto.command.ChangeTradeStatusCommand;
import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.application.trade.dto.command.RemoveTradeCommand;
import com.bob.core.application.trade.dto.result.ChangeTradeStatusResult;
import com.bob.core.application.trade.port.in.TradeCreator;
import com.bob.core.application.trade.port.in.TradeModifier;
import com.bob.core.application.trade.port.in.TradeReader;
import com.bob.core.application.trade.port.in.TradeRemover;
import com.bob.core.application.trade.port.out.TradeBookcasePort;
import com.bob.core.application.trade.port.out.TradeChatPort;
import com.bob.core.application.trade.port.out.TradeMemberPort;
import com.bob.core.application.trade.port.out.TradePostPort;
import com.bob.core.application.trade.port.result.TradeMember;
import com.bob.core.application.trade.port.result.TradePost;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.TradeRepository;
import com.bob.core.domain.trade.status.Status;
import com.bob.core.domain.trade.type.Owner;
import com.bob.global.event.application.dto.NotificationEvent;
import com.bob.global.event.application.dto.SystemChatMessageEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeCommandService implements TradeCreator, TradeModifier, TradeRemover {

    private final TradeRepository tradeRepository;
    private final TradeReader tradeReader;

    private final TradeMemberPort memberPort;
    private final TradeBookcasePort bookcasePort;
    private final TradePostPort postPort;
    private final TradeChatPort chatPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Trade create(CreateTradeCommand command) {
        TradePost post = postPort.read(command.postId());
        verifySelfTrade(post.sellerId(), command.buyerId());
        verifyTradePostAccessible(post);
        verifyTradePostWishOnly(post.wishOnly(), post.sellerId(), command.itemIds());

        return tradeRepository.findByPostIdAndBuyerId(post.id(), command.buyerId())
            .orElseGet(() -> {
                Trade trade = createTrade(command.postId(), post.sellerId(), command.buyerId(), command.isFar());

                trade.addItem(post.sellerBookId(), SELLER);

                command.itemIds().forEach(itemId -> trade.addItem(itemId, BUYER));
                bookcasePort.allocateUsageWithAuth(command.itemIds(), command.buyerId(), command.postId());

                TradeMember buyer = memberPort.readTradeMemberProfile(command.buyerId());

                final String notificationBody = REQUESTED_NOTI.format(buyer.nickname(), post.title());
                sendTradeNotification(post, command.buyerId(), post.sellerId(), notificationBody);

                return tradeRepository.save(trade);
            });
    }

    @Override
    public ChangeTradeStatusResult changeStatus(Long id, ChangeTradeStatusCommand command) {
        Trade trade = tradeReader.read(id);
        Status previous = trade.getStatus();
        Status status = valueOf(command.status());

        TradePost post = postPort.read(trade.getPostId());

        verifyTradeParticipate(trade, command.memberId());
        verifyTradeOwner(post.sellerId(), command.memberId(), status);
        verifyTradeChangeable(trade, status);
        verifyPostRequested(trade, command.status());

        Long chatroomId = changeTradeStatus(trade, status, post);

        UUID senderId = command.memberId();
        UUID receiverId = Objects.equals(senderId, trade.getSellerId()) ? trade.getBuyerId() : trade.getSellerId();

        String notificationBody = buildChangeStatusNotificationBody(post.title(), status, command.reason());
        sendTradeNotification(post, senderId, receiverId, notificationBody);

        if (trade.isProcessed() || trade.isRejected() || (previous != REQUESTED && trade.isCancelled())) {
            String chatMessageBody = buildChangeStatusChatMessageBody(status, command.reason());
            publishSystemMessageEvent(post, senderId, receiverId, chatMessageBody);
        }

        return new ChangeTradeStatusResult(trade, chatroomId);
    }

    private Long changeTradeStatus(Trade trade, Status status, TradePost post) {
        return switch (status) {
            case CANCELED, REJECTED -> onAborted(trade, status, post);
            case ACCEPTED -> onAccepted(trade);
            case RESERVED -> onReserved(trade);
            case COMPLETED -> onCompleted(trade, post);
            default -> null;
        };
    }

    @Override
    public Trade changeItems(Long id, ChangeTradeItemsCommand command) {
        Trade trade = tradeReader.read(id);
        TradePost post = postPort.read(trade.getPostId());
        verifyTradeParticipate(trade, command.memberId());
        verifyTradeItemChangeable(trade);
        verifyTradePostAccessible(post);

        Owner owner = trade.getBuyerId().equals(command.memberId()) ? BUYER : SELLER;
        if (trade.isAborted())
            trade.updateStatus(REQUESTED);

        if (owner == SELLER)
            verifyTradeMainItemContains(command.itemIds(), post.sellerBookId());

        changeTradeItems(trade, command, post.id(), owner);

        final String messageBody = CHANGED_TRADE_ITEM_CHAT.format();
        UUID receiverId = owner == SELLER ? trade.getBuyerId() : trade.getSellerId();
        publishSystemMessageEvent(post, command.memberId(), receiverId, messageBody);

        return trade;
    }

    private void changeTradeItems(Trade trade, ChangeTradeItemsCommand command, Long postId, Owner owner) {
        List<Long> current = trade.getItemIdsByOwner(owner);
        List<Long> requested = command.itemIds();
        verifyItemsChange(current, requested);

        List<Long> toRemove = new ArrayList<>(current);
        toRemove.removeAll(requested);
        bookcasePort.freeUsageWithAuth(toRemove, command.memberId());
        trade.removeItems(owner, current);

        List<Long> toAdd = new ArrayList<>(requested);
        toAdd.removeAll(current);
        bookcasePort.allocateUsageWithAuth(toAdd, command.memberId(), postId);
        requested.forEach(id -> trade.addItem(id, owner));

        tradeRepository.save(trade);
    }

    @Override
    public void remove(Long id, RemoveTradeCommand command) {
        Trade trade = tradeReader.read(id);
        verifyTradeBuyer(trade.getBuyerId(), command.requesterId());
        verifyTradeRemovable(trade);

        tradeRepository.delete(trade);
    }

    private Long onAborted(Trade trade, Status status, TradePost post) {
        changePostTradeProgressIfReserved(trade);
        trade.updateStatus(status);
        freeTradeItemsExcludeMainItem(trade, post.sellerBookId());
        return null;
    }

    private Long onAccepted(Trade trade) {
        changePostTradeProgressIfReserved(trade);
        trade.updateStatus(ACCEPTED);
        return chatPort.create(trade.getPostId(), trade.getId(), trade.getBuyerId(), trade.isFar());
    }

    private Long onReserved(Trade trade) {
        trade.updateStatus(RESERVED);
        postPort.changeTradeProgress(trade.getPostId(), RESERVED.toPostStatusValue());
        return null;
    }

    private Long onCompleted(Trade trade, TradePost post) {
        trade.updateStatus(COMPLETED);
        tradeRepository.cancelOtherTrades(trade.getPostId(), trade.getId());
        freeOtherTradeItems(trade.getPostId(), trade.getId(), post.sellerBookId());
        freeTradeItemsExcludeMainItem(trade, post.sellerBookId());

        List<Long> itemIds = trade.getAllItemIds();
        bookcasePort.delete(itemIds);
        trade.clearItems();

        postPort.changeTradeProgress(trade.getPostId(), COMPLETED.toPostStatusValue());
        return null;
    }

    private void freeTradeItemsExcludeMainItem(Trade trade, Long mainItemId) {
        List<Long> itemIds = trade.getAllItemIds().stream()
            .filter(id -> !id.equals(mainItemId))
            .toList();
        bookcasePort.freeUsage(itemIds);
    }

    private void freeOtherTradeItems(Long postId, Long tradeId, Long mainItemId) {
        List<Trade> otherTrades = tradeRepository.findAllByPostId(postId).stream()
            .filter(t -> !t.getId().equals(tradeId))
            .toList();

        List<Long> itemIds = otherTrades.stream()
            .flatMap(t -> t.getAllItemIds().stream())
            .filter(id -> !id.equals(mainItemId))
            .distinct()
            .toList();

        bookcasePort.freeUsage(itemIds);
    }

    private void changePostTradeProgressIfReserved(Trade trade) {
        if (trade.isReserved())
            postPort.changeTradeProgress(trade.getPostId(), REQUESTED.toPostStatusValue());
    }

    private void verifyTradeParticipate(Trade trade, UUID memberId) {
        if (!memberId.equals(trade.getSellerId()) && !memberId.equals(trade.getBuyerId()))
            throw new ApplicationException(TRADE_ACCESS_DENIED);
    }

    private void verifyTradePostAccessible(TradePost post) {
        if (Objects.equals(post.status(), "DEACTIVATED"))
            throw new ApplicationException(TRADE_POST_REMOVED);

        if (Objects.equals(post.tradeStatus(), "COMPLETED"))
            throw new ApplicationException(TRADE_ALREADY_PROCESSED);
    }

    private void verifyTradeItemChangeable(Trade trade) {
        if (trade.isProcessed())
            throw new ApplicationException(UNCHANGEABLE_TRADE_ITEM);
    }

    private void verifyTradeMainItemContains(List<Long> itemIds, Long mainItemId) {
        if (!itemIds.contains(mainItemId))
            throw new ApplicationException(TRADE_MAIN_ITEM_NOT_CONTAINED);
    }

    private void verifyPostRequested(Trade trade, String status) {
        if (!(valueOf(status) == RESERVED || valueOf(status) == COMPLETED))
            return;

        tradeRepository.findAllByPostId(trade.getPostId()).stream()
            .filter(t -> !Objects.equals(t.getId(), trade.getPostId()))
            .filter(Trade::isProcessed)
            .findAny()
            .ifPresent(t -> {
                throw new ApplicationException(TRADE_ALREADY_PROCESSED);
            });
    }

    private static void verifyTradeOwner(UUID ownerId, UUID requesterId, Status status) {
        if (status != CANCELED && !ownerId.equals(requesterId))
            throw new ApplicationException(TRADE_ACCESS_DENIED);
    }

    private static void verifyTradeChangeable(Trade trade, Status request) {
        Status current = trade.getStatus();

        if (current == request)
            throw new ApplicationException(TRADE_STATUS_UNCHANGED);

        if (current == COMPLETED)
            throw new ApplicationException(TRADE_ALREADY_COMPLETED);

        if (request == REQUESTED)
            throw new ApplicationException(TRADE_STATUS_NOT_CHANGEABLE);

        if (request == ACCEPTED && trade.isAborted())
            throw new ApplicationException(TRADE_ALREADY_ABORTED);
    }

    private static void verifyTradeBuyer(UUID buyerId, UUID requesterId) {
        if (!buyerId.equals(requesterId))
            throw new ApplicationException(TRADE_REMOVE_DENIED_BY_REQUESTER);
    }

    private static void verifyTradeRemovable(Trade trade) {
        if (!trade.isAborted())
            throw new ApplicationException(TRADE_REMOVE_DENIED_BY_STATUS);
    }

    private void verifySelfTrade(UUID sellerId, UUID buyerId) {
        if (Objects.equals(sellerId, buyerId))
            throw new ApplicationException(IS_SAME_TRADE_MEMBER);
    }

    private void verifyTradePostWishOnly(boolean wishOnly, UUID sellerId, List<Long> itemIds) {
        if (wishOnly && !memberPort.wishAllMatch(sellerId, itemIds))
            throw new ApplicationException(TRADE_SELLER_WISH_NOT_MATCH);
    }

    private void verifyItemsChange(List<Long> origin, List<Long> other) {
        if (new HashSet<>(origin).equals(new HashSet<>(other)))
            throw new ApplicationException(ApplicationError.TRADE_ITEMS_UNCHANGED);
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

    private void sendTradeNotification(TradePost post, UUID senderId, UUID receiverId, String body) {
        String postId = String.valueOf(post.id());
        NotificationEvent event = toSystemEvent(TRADE, postId, "SYSTEM", senderId, receiverId, body);
        eventPublisher.publishEvent(event);
    }

    private void publishSystemMessageEvent(TradePost post, UUID senderId, UUID receiverId, String body) {
        String postId = String.valueOf(post.id());
        SystemChatMessageEvent event = new SystemChatMessageEvent("TRADE", postId, senderId, receiverId, body);
        eventPublisher.publishEvent(event);
    }
}
