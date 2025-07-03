package com.bob.domain.chat.service.dto.response.internal;

import java.util.UUID;

public record ChatPartnerSummary(
    UUID id,
    String nickname,
    String profileUrl
) {

  public static ChatPartnerSummary of(UUID partnerId, String nickname, String profileUrl) {
    return new ChatPartnerSummary(partnerId, nickname, profileUrl);
  }
}
