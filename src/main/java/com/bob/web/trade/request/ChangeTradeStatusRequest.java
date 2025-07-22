package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChangeTradeStatusRequest(
    @NotNull(message = "구매자 ID는 필수입니다.")
    UUID buyerId,

    @NotBlank(message = "거래 상태는 필수입니다.")
    String status
) {

  public ChangeTradeStatusCommand toCommand(UUID memberId, Long tradeId) {
    return ChangeTradeStatusCommand.builder()
        .memberId(memberId)
        .tradeId(tradeId)
        .buyerId(buyerId)
        .status(status)
        .build();
  }
}
