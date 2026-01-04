package com.bob.core.trade.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.trade.domain.type.Owner;
import com.bob.shared.entity.AbstractEntity;

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
