package com.bob.domain.trade.service.dto.query;

import com.bob.domain.trade.entity.status.Status;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReadTradesQuery(
    UUID memberId,
    SearchKey key,
    List<Status> statuses
) {

  public static ReadTradesQuery of(UUID memberId, String key, List<String> statuses) {
    return ReadTradesQuery.builder()
        .memberId(memberId)
        .key(SearchKey.convertFrom(key))
        .statuses(Status.convertFrom(statuses))
        .build();
  }
}
