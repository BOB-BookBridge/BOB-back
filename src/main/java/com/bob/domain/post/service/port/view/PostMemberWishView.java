package com.bob.domain.post.service.port.view;

public record PostMemberWishView(
    String title,
    String author,
    String cover
) {

  public static PostMemberWishView of(String title, String author, String cover) {
    return new PostMemberWishView(title, author, cover);
  }
}
