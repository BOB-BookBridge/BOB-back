package com.bob.core.domain.trade;

import static com.bob.core.domain.trade.status.Status.CANCELED;
import static com.bob.core.domain.trade.status.Status.COMPLETED;
import static com.bob.core.domain.trade.status.Status.REJECTED;
import static com.bob.core.domain.trade.status.Status.RESERVED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;
import com.bob.core.domain.trade.status.Status;
import com.bob.core.domain.trade.type.Owner;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Trade extends AbstractEntity {

    private Long postId;

    private UUID sellerId;

    private UUID buyerId;

    private boolean isFar;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private List<TradeItem> items = new ArrayList<>();

    public static Trade createTrade(Long postId, UUID sellerId, UUID buyerId, boolean isFar) {
        return Trade.builder()
            .postId(postId)
            .sellerId(sellerId)
            .buyerId(buyerId)
            .isFar(isFar)
            .status(Status.REQUESTED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isProcessed() {
        return isReserved() || isCompleted();
    }

    public boolean isAborted() {
        return status == REJECTED || status == CANCELED;
    }

    public boolean isCancelled() {
        return status == CANCELED;
    }

    public boolean isRejected() {
        return status == REJECTED;
    }

    public boolean isReserved() {
        return status == RESERVED;
    }

    public boolean isCompleted() {
        return status == COMPLETED;
    }

    public void addItem(Long itemId, Owner owner) {
        TradeItem item = TradeItem.create(itemId, owner);
        items.add(item);
    }

    public void removeItems(Owner owner, List<Long> itemIds) {
        items.removeIf(item -> item.getOwner() == owner && itemIds.contains(item.getItemId()));
    }

    public List<Long> getItemIdsByOwner(Owner owner) {
        return items.stream()
            .filter(item -> item.getOwner() == owner)
            .map(TradeItem::getItemId)
            .toList();
    }

    public List<Long> getAllItemIds() {
        return items.stream()
            .map(TradeItem::getItemId)
            .toList();
    }

    public void clearItems() {
        items.clear();
    }
}
