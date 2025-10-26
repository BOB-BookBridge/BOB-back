package com.bob.domain.post.service.port.view;

public record PostMemberWishView(
    Long id,
    String title,
    String author,
    String cover
) {

  public static PostMemberWishView of(Long id, String title, String author, String cover) {
    return new PostMemberWishView(id, title, author, cover);
  }
}
