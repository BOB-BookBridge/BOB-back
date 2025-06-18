package com.bob.domain.member.service.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MemberAreaSummaryResponse(
    Integer emdId,
    Boolean validity,
    LocalDate authenticatedAt
) {

  public static MemberAreaSummaryResponse of(Integer emdId, Boolean validity, LocalDate authenticatedAt) {
    return MemberAreaSummaryResponse.builder()
        .emdId(emdId)
        .validity(validity)
        .authenticatedAt(authenticatedAt)
        .build();
  }
}
