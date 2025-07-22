package com.bob.domain.trade.entity;

import com.bob.domain.trade.entity.status.TradeStatus;
import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
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
@Table(name = "trades")
public class Trade extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  private UUID sellerId;

  @Column(nullable = false)
  private UUID buyerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TradeStatus tradeStatus;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public void updateTradeStatus(TradeStatus status, LocalDateTime now) {
    this.tradeStatus = status;
    this.updatedAt = now;
  }
}
