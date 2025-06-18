package com.bob.domain.post.service.dto.response;

import lombok.Builder;

@Builder
public record PostAreaSummaryResponse(
    int emdId,
    String emdName,
    String siggName,
    boolean validity
) {

  public static PostAreaSummaryResponse of(int emdId, String emdName, String siggName, boolean validity) {
    return PostAreaSummaryResponse.builder()
        .emdId(emdId)
        .emdName(emdName)
        .siggName(siggName)
        .validity(validity)
        .build();
  }
}
