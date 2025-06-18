package com.bob.domain.post.service.dto.query;

import java.util.UUID;

public record ReadPostFavoritesQuery(
    UUID memberId
) {

  public static ReadPostFavoritesQuery of(UUID memberId) {
    return new ReadPostFavoritesQuery(memberId);
  }
}
