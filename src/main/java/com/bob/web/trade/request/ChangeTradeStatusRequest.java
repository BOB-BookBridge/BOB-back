package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ChangeTradeStatusRequest(
    @NotBlank(message = "거래 상태는 필수입니다.")
    String status,

    @Nullable
    @Size(max = 200, message = "취소 사유는 200자 이하로 입력해주세요.")
    String reason
) {

  public ChangeTradeStatusCommand toCommand(UUID memberId, Long tradeId) {
    return ChangeTradeStatusCommand.builder()
        .memberId(memberId)
        .tradeId(tradeId)
        .status(status)
        .reason(reason)
        .build();
  }
}
