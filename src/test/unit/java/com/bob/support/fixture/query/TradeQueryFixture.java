package com.bob.support.fixture.query;

import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import java.util.List;
import java.util.UUID;

public class TradeQueryFixture {

  public static ReadTradesQuery KEY_NULL_STATUS_NULL(UUID memberId) {
    return ReadTradesQuery.of(memberId, null, null);
  }

  public static ReadTradesQuery SENT_STATUS_REQUESTED_REJECTED(UUID memberId) {
    return ReadTradesQuery.of(memberId, "SENT", List.of("REQUESTED", "REJECTED"));
  }

  public static ReadTradesQuery RECEIVED_STATUS_REQUESTED(UUID memberId) {
    return ReadTradesQuery.of(memberId, "RECEIVED", List.of("REQUESTED"));
  }
}
