package com.bob.support.fixture.response.post;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.post.PostMemberWishesViewFixture.DEFAULT_POST_MEMBER_WISHES_VIEW;

import com.bob.domain.post.service.port.view.PostMemberView;
import java.util.List;

public class PostMemberViewFixture {

  public static final PostMemberView DEFAULT_POST_MEMBER_VIEW =
      PostMemberView.of(
          MEMBER_ID,
          "tester",
          213,
          "http://image.url",
          List.of("관심사1", "관심사2"),
          DEFAULT_POST_MEMBER_WISHES_VIEW
      );

}
