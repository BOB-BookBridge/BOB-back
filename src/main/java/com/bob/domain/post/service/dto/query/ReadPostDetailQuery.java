package com.bob.domain.post.service.dto.query;

import java.util.UUID;

public record ReadPostDetailQuery(
    UUID memberId,
    Long postId,
    boolean shouldIncreaseViewCount
) {

  public static ReadPostDetailQuery of(UUID memberId, Long postId, boolean flag) {
    return new ReadPostDetailQuery(memberId, postId, flag);
  }
}
