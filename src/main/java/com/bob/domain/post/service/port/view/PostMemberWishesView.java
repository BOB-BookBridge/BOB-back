package com.bob.domain.post.service.port.view;

import java.util.List;

public record PostMemberWishesView(
    List<PostMemberWishView> wishes
) {

  public static PostMemberWishesView from(List<PostMemberWishView> wishes) {
    return new PostMemberWishesView(wishes);
  }
}
