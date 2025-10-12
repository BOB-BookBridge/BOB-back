package com.bob.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
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
@Table(name = "member_books")
public class MemberBook {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private Long bookId;

  @Column
  private Long usageId;

  @Column(nullable = false)
  private boolean isRemove;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookStatus status;

  public static MemberBook of(UUID memberId, Long bookId, String status) {
    return MemberBook.builder()
        .memberId(memberId)
        .bookId(bookId)
        .status(BookStatus.valueOf(status))
        .isRemove(false)
        .build();
  }

  public void updateUsageId(Long usageId) {
    this.usageId = usageId;
  }

  public void remove() {
    this.isRemove = true;
  }

  public boolean isOwner(UUID memberId) {
    return Objects.equals(this.memberId, memberId);
  }

  public boolean isRemovable() {
    return usageId == null;
  }
}
