package com.bob.web.post.request;

import com.bob.domain.post.service.dto.query.ReadPostFavoritesQuery;
import java.util.UUID;

public record ReadPostFavoritesRequest(

) {

  public static ReadPostFavoritesQuery toQuery(UUID memberId) {
    return new ReadPostFavoritesQuery(memberId);
  }
}
