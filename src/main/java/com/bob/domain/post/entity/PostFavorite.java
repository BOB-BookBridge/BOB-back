package com.bob.domain.post.entity;

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
@Table(name = "post_favorites",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "post_id"})
    }
)
public class PostFavorite extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private Long postId;

  public static PostFavorite create(UUID memberId, Long postId) {
    return PostFavorite.builder()
        .memberId(memberId)
        .postId(postId)
        .build();
  }
}
