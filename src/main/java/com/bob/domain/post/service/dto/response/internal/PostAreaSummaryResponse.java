package com.bob.domain.post.service.dto.response.internal;

import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import lombok.Builder;

@Builder
public record PostAreaSummaryResponse(
    int emdId,
    String emdName,
    String siggName,
    boolean validity
) {

  public static PostAreaSummaryResponse from(AreaSummaryResponse response) {
    return PostAreaSummaryResponse.builder()
        .emdId(response.emdId())
        .emdName(response.emdName())
        .siggName(response.siggName())
        .validity(response.validity())
        .build();
  }
}
