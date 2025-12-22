package com.bob.core.bookcase.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookcaseItem extends AbstractEntity {

    private UUID memberId;

    private Long bookId;

    private Long usageId;

    private LocalDateTime deletedAt;

    private BookStatus status;

    public static BookcaseItem createBookcaseItem(UUID memberId, Long bookId, String status) {
        return BookcaseItem.builder()
            .memberId(memberId)
            .bookId(bookId)
            .status(BookStatus.valueOf(status))
            .build();
    }

    public boolean isOwner(UUID memberId) {
        return Objects.equals(this.memberId, memberId);
    }

    public void updateUsageId(Long usageId) {
        this.usageId = usageId;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isDeletable() {
        return usageId == null;
    }
}
