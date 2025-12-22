package com.bob.core.trade.adapter.api.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateTradeRequest(
    Long postId,

    @NotEmpty(message = "거래 물품은 필수입니다.")
    List<Long> itemIds,

    @NotNull(message = "거리 여부는 필수입니다.")
    Boolean isFar
) {

}
