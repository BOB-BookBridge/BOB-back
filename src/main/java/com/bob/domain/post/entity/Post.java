package com.bob.domain.post.entity;

import com.bob.domain.book.entity.Book;
import com.bob.domain.category.entity.Category;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.status.PostStatus;
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
  private PostStatus postStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(nullable = false)
  private UUID sellerId;

  @Column(nullable = false)
  private String thumbnailUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookStatus bookStatus;

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

  public void updateOptionalFields(Integer sellPrice, String bookStatus, String description) {
    Optional.ofNullable(sellPrice).ifPresent(s -> this.sellPrice = s);
    Optional.ofNullable(bookStatus).ifPresent(b -> this.bookStatus = BookStatus.from(b));
    Optional.ofNullable(description).ifPresent(d -> this.description = d);
  }
}