package com.bob.support.fixture.query;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;

public class MemberQueryFixture {

  public static final ReadProfileQuery READ_ME_PROFILE_QUERY = new ReadProfileQuery(MEMBER_ID, true);
  public static final ReadProfileQuery READ_OTHER_PROFILE_QUERY = new ReadProfileQuery(MEMBER_ID, false);
}
