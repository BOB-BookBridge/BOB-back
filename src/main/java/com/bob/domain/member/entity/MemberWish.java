package com.bob.domain.member.entity;

import com.bob.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "member_wishes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_wish_book", columnNames = {"member_id", "book_id"})
    }
)
public class MemberWish extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private Long bookId;

  public static MemberWish create(UUID memberId, Long bookId) {
    return MemberWish.builder()
        .memberId(memberId)
        .bookId(bookId)
        .build();
  }
}
