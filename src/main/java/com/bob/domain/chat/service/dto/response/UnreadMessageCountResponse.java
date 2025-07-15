package com.bob.domain.chat.service.dto.response;

public record UnreadMessageCountResponse(
    Integer unreadCount
) {

  public static UnreadMessageCountResponse of(Integer unreadCount) {
    return new UnreadMessageCountResponse(unreadCount);
  }
}
