package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.service.dto.response.internal.PostTradeSummary;
import java.util.List;

public record PostTradesResponse(
    List<PostTradeSummary> trades
) {

  public static PostTradesResponse of(List<PostTradeSummary> trades) {
    return new PostTradesResponse(trades);
  }
}
