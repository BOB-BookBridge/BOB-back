package com.bob.support.fixture.query;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.query.ReadProfileWithPostsQuery;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class MemberQueryFixture {

  public static ReadProfileQuery defaultReadProfileQuery() {
    return new ReadProfileQuery(MEMBER_ID, false);
  }

  public static ReadProfileWithPostsQuery defaultReadProfileWithPostsQuery(UUID memberId) {
    Pageable pageable = PageRequest.of(0, 12);
    return new ReadProfileWithPostsQuery(memberId, pageable);
  }
}
