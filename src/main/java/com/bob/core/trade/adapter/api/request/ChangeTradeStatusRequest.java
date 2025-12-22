package com.bob.core.trade.adapter.api.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeTradeStatusRequest(
    @NotBlank(message = "거래 상태는 필수입니다.")
    String status,

    @Nullable
    @Size(max = 200, message = "취소 사유는 200자 이하로 입력해주세요.")
    String reason
) {

}
