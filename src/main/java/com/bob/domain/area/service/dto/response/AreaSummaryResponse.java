package com.bob.domain.area.service.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record AreaSummaryResponse(
    Integer emdId,
    String emdName,
    String siggName,
    Boolean validity,
    LocalDate authenticatedAt
) {

  public static AreaSummaryResponse of(
      Integer emdId,
      String emdName,
      String siggName,
      Boolean validity,
      LocalDate authenticatedAt
  ) {
    return AreaSummaryResponse.builder()
        .emdId(emdId)
        .emdName(emdName)
        .siggName(siggName)
        .validity(validity)
        .authenticatedAt(authenticatedAt)
        .build();
  }
}
