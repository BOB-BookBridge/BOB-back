package com.bob.web.chat.request;

import com.bob.domain.chat.service.dto.command.CreateChatRoomCommand;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateChatRoomRequest(
    @NotNull(message = "게시글 ID는 필수입니다.")
    Long postId,

    @NotNull(message = "거리 여부는 필수입니다.")
    Boolean isFar
) {

  public CreateChatRoomCommand toCommand(UUID memberId) {
    return new CreateChatRoomCommand(postId, memberId, isFar);
  }
}
