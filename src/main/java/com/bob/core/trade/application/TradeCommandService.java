package com.bob.core.trade.application;

import static com.bob.core.trade.application.util.TradeMessageTemplate.CANCELED_WITH_REASON_CHAT;
import static com.bob.core.trade.application.util.TradeMessageTemplate.CANCELED_WITH_REASON_NOTI;
import static com.bob.core.trade.application.util.TradeMessageTemplate.CHANGED_TRADE_ITEM_CHAT;
import static com.bob.core.trade.application.util.TradeMessageTemplate.REQUESTED_NOTI;
import static com.bob.core.trade.application.util.TradeMessageTemplate.STATUS_CHANGED_CHAT;
import static com.bob.core.trade.application.util.TradeMessageTemplate.STATUS_CHANGED_NOTI;
import static com.bob.core.trade.domain.Trade.createTrade;
import static com.bob.core.trade.domain.status.Status.ACCEPTED;
import static com.bob.core.trade.domain.status.Status.CANCELED;
import static com.bob.core.trade.domain.status.Status.COMPLETED;
import static com.bob.core.trade.domain.status.Status.REQUESTED;
import static com.bob.core.trade.domain.status.Status.RESERVED;
import static com.bob.core.trade.domain.status.Status.valueOf;
import static com.bob.core.trade.domain.type.Owner.BUYER;
import static com.bob.core.trade.domain.type.Owner.SELLER;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ITEM_UNCHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_MAIN_ITEM_UNCHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_POST_REMOVED;
import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_ONLY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_ONLY_REQUESTER;
import static com.bob.global.exception.response.ApplicationError.TRADE_SELF_NOT_ALLOWED;
import static com.bob.global.exception.response.ApplicationError.TRADE_SELLER_WISH_NOT_MATCH;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_ALREADY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_ALREADY_COMPLETED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_NOT_CHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.trade.application.dto.command.ChangeTradeItemsCommand;
import com.bob.core.trade.application.dto.command.ChangeTradeStatusCommand;
import com.bob.core.trade.application.dto.command.CreateTradeCommand;
import com.bob.core.trade.application.dto.command.RemoveTradeCommand;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.application.port.in.TradeCreator;
import com.bob.core.trade.application.port.in.TradeModifier;
import com.bob.core.trade.application.port.in.TradeReader;
import com.bob.core.trade.application.port.in.TradeRemover;
import com.bob.core.trade.application.port.out.TradeBookcasePort;
import com.bob.core.trade.application.port.out.TradeChatPort;
import com.bob.core.trade.application.port.out.TradeMemberPort;
import com.bob.core.trade.application.port.out.TradePostPort;
import com.bob.core.trade.application.port.result.TradeMember;
import com.bob.core.trade.application.port.result.TradePost;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.core.trade.domain.status.Status;
import com.bob.core.trade.domain.type.Owner;
import com.bob.core.trade.event.TradeChangedEvent;
import com.bob.core.trade.event.TradeNotificationEvent;
import com.bob.core.trade.event.TradeStatusChangedEvent;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.snapshot.RecordSnapshot;

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
    @RecordSnapshot
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
                publishTradeNotificationEvent(post, command.buyerId(), post.sellerId(), notificationBody);

                return tradeRepository.save(trade);
            });
    }

    @Override
    @RecordSnapshot
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
        publishTradeNotificationEvent(post, senderId, receiverId, notificationBody);

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

        publishStatusChangedEvent(trade, RESERVED);

        return null;
    }

    private Long onCompleted(Trade trade, TradePost post) {
        trade.updateStatus(COMPLETED);
        tradeRepository.cancelOtherTrades(trade.getPostId(), trade.getId());
        freeOtherTradeItems(trade.getPostId(), trade.getId(), post.sellerBookId());

        List<Long> itemIds = trade.getAllItemIds();
        bookcasePort.delete(itemIds);

        publishStatusChangedEvent(trade, COMPLETED);

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
            publishStatusChangedEvent(trade, REQUESTED);
    }

    private void publishStatusChangedEvent(Trade trade, Status newStatus) {
        String postStatusValue = newStatus.toPostStatusValue();
        TradeStatusChangedEvent event = new TradeStatusChangedEvent(trade.getId(), trade.getPostId(), postStatusValue);

        eventPublisher.publishEvent(event);
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
            throw new ApplicationException(TRADE_ITEM_UNCHANGEABLE);
    }

    private void verifyTradeMainItemContains(List<Long> itemIds, Long mainItemId) {
        if (!itemIds.contains(mainItemId))
            throw new ApplicationException(TRADE_MAIN_ITEM_UNCHANGEABLE);
    }

    private void verifyPostRequested(Trade trade, String status) {
        if (!(valueOf(status) == RESERVED || valueOf(status) == COMPLETED))
            return;

        tradeRepository.findAllByPostId(trade.getPostId()).stream()
            .filter(t -> !Objects.equals(t.getId(), trade.getId()))
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
            throw new ApplicationException(TRADE_STATUS_ALREADY_COMPLETED);

        if (request == REQUESTED)
            throw new ApplicationException(TRADE_STATUS_NOT_CHANGEABLE);

        if ((request == ACCEPTED || request == RESERVED || request == COMPLETED) && trade.isAborted())
            throw new ApplicationException(TRADE_STATUS_ALREADY_ABORTED);
    }

    private static void verifyTradeBuyer(UUID buyerId, UUID requesterId) {
        if (!buyerId.equals(requesterId))
            throw new ApplicationException(TRADE_REMOVE_ONLY_REQUESTER);
    }

    private static void verifyTradeRemovable(Trade trade) {
        if (!trade.isAborted())
            throw new ApplicationException(TRADE_REMOVE_ONLY_ABORTED);
    }

    private void verifySelfTrade(UUID sellerId, UUID buyerId) {
        if (Objects.equals(sellerId, buyerId))
            throw new ApplicationException(TRADE_SELF_NOT_ALLOWED);
    }

    private void verifyTradePostWishOnly(boolean wishOnly, UUID sellerId, List<Long> itemIds) {
        if (wishOnly && !memberPort.wishAllMatch(sellerId, itemIds))
            throw new ApplicationException(TRADE_SELLER_WISH_NOT_MATCH);
    }

    private void verifyItemsChange(List<Long> origin, List<Long> other) {
        if (new HashSet<>(origin).equals(new HashSet<>(other)))
            throw new ApplicationException(ApplicationError.NO_CHANGES);
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

    private void publishTradeNotificationEvent(TradePost post, UUID senderId, UUID receiverId, String body) {
        TradeNotificationEvent event = new TradeNotificationEvent(post.id(), senderId, receiverId, body);

        eventPublisher.publishEvent(event);
    }

    private void publishSystemMessageEvent(TradePost post, UUID senderId, UUID receiverId, String body) {
        TradeChangedEvent event = new TradeChangedEvent(post.id(), senderId, receiverId, body);

        eventPublisher.publishEvent(event);
    }
}
