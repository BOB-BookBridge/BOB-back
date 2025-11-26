package com.bob.core.application.post.port.result;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record PostMember(
    UUID id, String nickname, int emdId, boolean authenticated, String profileImageUrl,
    List<String> interests, List<PostMemberWishResult> wishes
) {

}
