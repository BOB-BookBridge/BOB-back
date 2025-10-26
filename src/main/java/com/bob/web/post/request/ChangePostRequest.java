package com.bob.web.post.request;

import com.bob.domain.post.service.dto.command.ChangePostCommand;
import com.bob.global.utils.web.validator.AtLeastOneNotNull;
import java.util.UUID;

@AtLeastOneNotNull(anyOf = {"sellPrice", "bookStatus", "description", "wishOnly"})
public record ChangePostRequest(
    Integer sellPrice,
    String bookStatus,
    String description,
    Boolean wishOnly
) {

  // TODO: [remove] request to command 변환은 command에서 수행
  public ChangePostCommand toCommand(Long postId, UUID memberId) {
    return ChangePostCommand.builder()
        .postId(postId)
        .memberId(memberId)
        .sellPrice(sellPrice)
        .bookStatus(bookStatus)
        .description(description)
        .wishOnly(wishOnly)
        .build();
  }
}
