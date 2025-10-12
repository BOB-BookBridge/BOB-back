package com.bob.domain.post.entity;

import static com.bob.domain.post.entity.status.Status.ACTIVE;
import static com.bob.domain.post.entity.status.TradeProgress.READY;

import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.status.Status;
import com.bob.domain.post.entity.status.TradeProgress;
import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Optional;
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
@Table(name = "posts")
public class Post extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(length = 50, nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(nullable = false)
  private Long bookId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookStatus bookStatus;

  @Column(nullable = false)
  private UUID sellerId;

  @Column(nullable = false)
  private String thumbnailUrl;

  @Column(nullable = false)
  private Integer sellPrice;

  @Column(length = 200)
  private String description;

  @Column(nullable = false)
  private Integer registrationAreaId;

  @Column(nullable = false)
  @Builder.Default
  private Integer viewCount = 0;

  @Column(nullable = false)
  @Builder.Default
  private Integer scrapCount = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TradeProgress tradeProgress;

  public static Post create(Category category, Long bookId, int emdId,
      String title, String description, String thumbnailUrl, String bookStatus, UUID sellerId, int sellPrice
  ) {
    return Post.builder()
        .status(ACTIVE)
        .category(category)
        .title(title)
        .description(description)
        .bookId(bookId)
        .bookStatus(BookStatus.from(bookStatus))
        .sellerId(sellerId)
        .sellPrice(sellPrice)
        .tradeProgress(READY)
        .registrationAreaId(emdId)
        .thumbnailUrl(thumbnailUrl)
        .build();
  }

  public void updateOptionalFields(Integer sellPrice, String bookStatus, String description) {
    Optional.ofNullable(sellPrice).ifPresent(s -> this.sellPrice = s);
    Optional.ofNullable(bookStatus).ifPresent(b -> this.bookStatus = BookStatus.from(b));
    Optional.ofNullable(description).ifPresent(d -> this.description = d);
  }

  public void updateTradeProgress(TradeProgress status) {
    this.tradeProgress = status;
  }

  public void updateStatus(Status status) {
    this.status = status;
  }
}
