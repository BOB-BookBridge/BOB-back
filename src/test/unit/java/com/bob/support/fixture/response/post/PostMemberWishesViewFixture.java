package com.bob.support.fixture.response.post;

import static com.bob.support.fixture.response.MemberWishesResultFixture.DEFAULT_MEMBER_WISHES_RESULT;

import com.bob.domain.post.service.port.view.PostMemberWishView;
import com.bob.domain.post.service.port.view.PostMemberWishesView;

public class PostMemberWishesViewFixture {

  public static PostMemberWishesView DEFAULT_POST_MEMBER_WISHES_VIEW =
      PostMemberWishesView.from(DEFAULT_MEMBER_WISHES_RESULT.wishes().stream()
          .map(wish -> PostMemberWishView.of(wish.id(), wish.title(), wish.author(), wish.cover()))
          .toList());
}
