package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;

import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import java.time.LocalDate;

public class MemberAreaSummaryResponseFixture {

  public static final MemberAreaSummaryResponse DEFAULT_AREA_SUMMARY_RESPONSE = MemberAreaSummaryResponse.builder()
      .emdId(EMD_AREA_ID)
      .validity(true)
      .authenticatedAt(LocalDate.now())
      .build();
}
