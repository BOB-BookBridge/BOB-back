package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ChangeTradeStatusRequest(
    @NotBlank(message = "거래 상태는 필수입니다.")
    String status
) {

  public ChangeTradeStatusCommand toCommand(UUID memberId, Long tradeId) {
    return ChangeTradeStatusCommand.builder()
        .memberId(memberId)
        .tradeId(tradeId)
        .status(status)
        .build();
  }
}
