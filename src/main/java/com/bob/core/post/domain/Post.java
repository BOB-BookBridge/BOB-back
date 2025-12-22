package com.bob.core.post.domain;

import static com.bob.core.post.domain.PostFavorite.createPostFavorite;
import static com.bob.core.post.domain.status.Status.ACTIVE;
import static com.bob.core.post.domain.status.Status.DEACTIVATED;
import static com.bob.core.post.domain.status.TradeProgress.READY;
import static com.bob.core.post.domain.status.TradeProgress.RESERVED;
import static org.springframework.util.Assert.state;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.post.domain.status.BookStatus;
import com.bob.core.post.domain.status.Status;
import com.bob.core.post.domain.status.TradeProgress;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {

    @Id
    private Long id;

    private Status status;

    private Integer categoryId;

    private String title;

    private String thumbnailUrl;

    private String description;

    private Integer price;

    private Integer registrationAreaId;

    private Long bookId;

    private BookStatus bookStatus;

    private UUID writerId;

    private Long writerBookId;

    private TradeProgress tradeProgress;

    private boolean wishOnly;

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer scrapCount = 0;

    @Builder.Default
    private List<PostFavorite> favorites = new ArrayList<>();

    private LocalDateTime createdAt;

    public static Post createPost(Integer categoryId, int emdId, Long bookId,
        String title, String description, String thumbnailUrl, String bookStatus,
        UUID writerId, Long sellerBookId, Integer price, boolean wishOnly
    ) {
        return Post.builder()
            .status(ACTIVE)
            .categoryId(categoryId)
            .bookId(bookId)
            .title(title)
            .description(description)
            .bookStatus(BookStatus.from(bookStatus))
            .writerId(writerId)
            .writerBookId(sellerBookId)
            .price(price)
            .tradeProgress(READY)
            .registrationAreaId(emdId)
            .thumbnailUrl(thumbnailUrl)
            .wishOnly(wishOnly)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void updateInfo(String bookStatus, String description, Boolean wishOnly) {
        Optional.ofNullable(bookStatus).ifPresent(b -> this.bookStatus = BookStatus.from(b));
        Optional.ofNullable(description).ifPresent(d -> this.description = d);
        Optional.ofNullable(wishOnly).ifPresent(d -> this.wishOnly = wishOnly);
    }

    public void updateTradeProgress(TradeProgress status) {
        this.tradeProgress = status;
    }

    public void activate() {
        this.status = ACTIVE;
    }

    public void deactivate() {
        state(status == ACTIVE, "활성 상태가 아닙니다.");

        this.status = DEACTIVATED;
        this.favorites.clear();
    }

    public void addFavorite(UUID memberId) {
        PostFavorite favorite = createPostFavorite(memberId);

        this.favorites.add(favorite);
    }

    public void removeFavorite(UUID memberId) {
        this.favorites.removeIf(favorite -> Objects.equals(favorite.getMemberId(), memberId));
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public boolean isDeactivated() {
        return status == DEACTIVATED;
    }

    public boolean isReserved() {
        return tradeProgress == RESERVED;
    }

    public boolean isFavorite(UUID memberId) {
        return this.favorites.stream()
            .anyMatch(favorite -> Objects.equals(favorite.getMemberId(), memberId));
    }
}
