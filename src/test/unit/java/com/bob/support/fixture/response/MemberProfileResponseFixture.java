package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.MemberWishFixture.CUSTOM_MEMBER_WISH;
import static com.bob.support.fixture.domain.MemberWishFixture.DEFAULT_MEMBER_WISHES;
import static com.bob.support.fixture.response.BookResponseFixture.CUSTOM_BOOK_RESPONSE;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MemberProfileResponseFixture {

  public static final MemberProfileResponse DEFAULT_MEMBER_PROFILE_RESPONSE =
      MemberProfileResponse.builder()
          .memberId(MEMBER_ID)
          .nickname("tester")
          .profileImageUrl("http://image.url")
          .area(new MemberProfileResponse.Area(213, true, LocalDate.now()))
          .interests(List.of("관심사1", "관심사2"))
          .wishes(MemberWishSummary.listFrom(DEFAULT_MEMBER_WISHES, DEFAULT_BOOK_RESPONSES))
          .build();

  public static final MemberProfileResponse OTHER_MEMBER_PROFILE_RESPONSE =
      MemberProfileResponse.builder()
          .memberId(OTHER_MEMBER_ID)
          .nickname("other")
          .profileImageUrl("http://image.url")
          .area(new MemberProfileResponse.Area(1, true, LocalDate.now()))
          .interests(List.of("관심사1", "관심사2"))
          .wishes(MemberWishSummary.listFrom(List.of(CUSTOM_MEMBER_WISH(6L, 6L)), List.of(CUSTOM_BOOK_RESPONSE(6L))))
          .build();

  public static MemberProfileResponse CUSTOM_MEMBER_PROFILE_RESPONSE(UUID memberId) {
    return MemberProfileResponse.builder()
        .memberId(memberId)
        .nickname("custom")
        .profileImageUrl("http://image.url")
        .area(new MemberProfileResponse.Area(1, true, LocalDate.now()))
        .build();
  }

}
