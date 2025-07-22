package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadFilteredTradesRequest(
    Long postId
) {

  public ReadTradesQuery toQuery(UUID memberId) {
    return new ReadTradesQuery(postId, memberId);
  }
}
