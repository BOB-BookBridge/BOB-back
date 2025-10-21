package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateTradeRequest(
    Long postId,

    @NotEmpty(message = "거래 물품은 필수입니다.")
    List<Long> itemIds,

    @NotNull(message = "거리 여부는 필수입니다.")
    Boolean isFar
) {

  public CreateTradeCommand toCommand(UUID memberId) {
    return CreateTradeCommand.of(postId, memberId, itemIds, isFar);
  }
}
