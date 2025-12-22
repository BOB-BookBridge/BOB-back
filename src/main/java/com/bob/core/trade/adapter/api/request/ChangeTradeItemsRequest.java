package com.bob.core.trade.adapter.api.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ChangeTradeItemsRequest(
    @NotEmpty(message = "거래 물품은 필수입니다.")
    List<Long> itemIds
) {

}
