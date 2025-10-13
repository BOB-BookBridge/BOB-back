package com.bob.domain.trade.entity;

import com.bob.domain.trade.entity.type.Owner;
import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "trade_items",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"trade_id", "item_id"})
    }
)
public class TradeItem extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long tradeId;

  @Column(nullable = false)
  private Long itemId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Owner owner;

  public static TradeItem create(Long tradeId, Long itemId, Owner owner) {
    return TradeItem.builder()
        .tradeId(tradeId)
        .itemId(itemId)
        .owner(owner)
        .build();
  }
}
