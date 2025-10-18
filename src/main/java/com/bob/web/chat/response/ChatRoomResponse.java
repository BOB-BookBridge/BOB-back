package com.bob.web.chat.response;

import com.bob.domain.chat.service.dto.response.ChatRoomResult;
import java.util.UUID;

public record ChatRoomResponse(
    Long chatroomId,
    String title,
    Trade trade,
    Post post,
    Partner partner
) {
  public static ChatRoomResponse from(ChatRoomResult result, String tradeStatus) {
    return new ChatRoomResponse(
        result.chatroomId(),
        result.title(),
        Trade.of(result.trade().id(), tradeStatus),
        Post.of(
            result.post().id(),
            result.post().status(),
            result.post().sellerId(),
            result.post().title(),
            result.post().thumbnailUrl(),
            result.post().sellPrice()
        ),
        Partner.of(
            result.partner().id(),
            result.partner().nickname(),
            result.partner().profileUrl()
        )
    );
  }

  public record Trade(Long id, String status) {
    public static Trade of(Long id, String status) { return new Trade(id, status); }
  }

  public record Post(
      Long id,
      String status,
      UUID sellerId,
      String title,
      String thumbnailUrl,
      int sellPrice
  ) {
    public static Post of(Long id, String status, UUID sellerId, String title, String thumbnailUrl, int sellPrice) {
      return new Post(id, status, sellerId, title, thumbnailUrl, sellPrice);
    }
  }

  public record Partner(UUID id, String nickname, String profileUrl) {
    public static Partner of(UUID id, String nickname, String profileUrl) {
      return new Partner(id, nickname, profileUrl);
    }
  }
}