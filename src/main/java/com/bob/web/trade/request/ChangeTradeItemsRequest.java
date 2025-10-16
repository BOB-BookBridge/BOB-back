package com.bob.web.trade.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ChangeTradeItemsRequest(
    @NotEmpty(message = "거래 물품은 필수입니다.")
    List<Long> itemIds
) {

}
