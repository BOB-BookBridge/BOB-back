package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;

import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import java.time.LocalDate;

public class AreaSummaryResponseFixture {

  public static final AreaSummaryResponse DEFAULT_AREA_SUMMARY = AreaSummaryResponse.builder()
      .emdId(EMD_AREA_ID)
      .emdName("역삼동")
      .siggName("강남구")
      .validity(true)
      .authenticatedAt(LocalDate.now())
      .build();
}
