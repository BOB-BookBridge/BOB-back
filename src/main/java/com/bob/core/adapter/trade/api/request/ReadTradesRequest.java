package com.bob.core.adapter.trade.api.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

import com.bob.core.adapter.trade.api.request.validator.ValidTradeKey;
import com.bob.core.adapter.trade.api.request.validator.ValidTradeStatus;

public record ReadTradesRequest(
    @ValidTradeKey
    String key,

    @Nullable
    @Size(min = 1, message = "상태 목록은 비어 있을 수 없습니다.")
    List<@ValidTradeStatus String> status
) {

}
