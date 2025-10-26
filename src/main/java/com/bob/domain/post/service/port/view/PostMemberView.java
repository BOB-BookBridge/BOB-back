package com.bob.domain.post.service.port.view;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PostMemberView(
    UUID memberId,
    String nickname,
    int emdId,
    String profileUrl,
    List<String> interests,
    List<PostMemberWishView> wishes
) {

  public static PostMemberView of(
      UUID memberId,
      String nickname,
      int emdId,
      String profileUrl,
      List<String> interests,
      PostMemberWishesView wishesView
  ) {
    return PostMemberView.builder()
        .memberId(memberId)
        .nickname(nickname)
        .emdId(emdId)
        .profileUrl(profileUrl)
        .interests(interests)
        .wishes(wishesView.wishes())
        .build();
  }
}
