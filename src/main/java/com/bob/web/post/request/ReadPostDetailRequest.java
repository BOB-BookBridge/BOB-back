package com.bob.web.post.request;

import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import java.util.UUID;

public record ReadPostDetailRequest(
    Long postId
) {

  public static ReadPostDetailQuery toQuery(UUID memberId, Long postId) {
    return new ReadPostDetailQuery(memberId, postId, true);
  }
}
