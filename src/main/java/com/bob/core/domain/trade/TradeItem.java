package com.bob.core.domain.trade;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;
import com.bob.core.domain.trade.type.Owner;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeItem extends AbstractEntity {

    private Long itemId;

    private Owner owner;

    private LocalDateTime createdAt;

    public static TradeItem create(Long itemId, Owner owner) {
        return TradeItem.builder()
            .itemId(itemId)
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
