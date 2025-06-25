package com.bob.domain.post.service.dto.response;

public record PostCreateResponse(
    Long postId
) {

  public static PostCreateResponse of(Long postId) {
    return new PostCreateResponse(postId);
  }
}
