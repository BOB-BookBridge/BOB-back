package com.bob.support.fixture.response;

import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;

public class PostMemberSummaryResponseFixture {

  public static final PostMemberSummaryResponse DEFAULT_MEMBER_SUMMARY = PostMemberSummaryResponse.builder()
      .nickname("tester")
      .profileImageUrl("url")
      .build();
}
