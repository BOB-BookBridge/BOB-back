package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;

import com.bob.domain.post.service.dto.response.PostAreaSummaryResponse;

public class PostAreaSummaryResponseFixture {

  public static final PostAreaSummaryResponse DEFAULT_POST_AREA_SUMMARY = PostAreaSummaryResponse.builder()
      .emdId(EMD_AREA_ID)
      .emdName("역삼동")
      .siggName("강남구")
      .validity(true)
      .build();

  public static final PostAreaSummaryResponse NOT_VALID_POST_AREA_SUMMARY = PostAreaSummaryResponse.builder()
      .emdId(EMD_AREA_ID)
      .emdName("역삼동")
      .siggName("강남구")
      .validity(false)
      .build();
}
