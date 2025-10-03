package com.bob.web.trade.request;

import com.bob.web.trade.request.validator.ValidTradeKey;
import com.bob.web.trade.request.validator.ValidTradeStatus;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReadTradesRequest(
    @ValidTradeKey
    String key,

    @Size(min = 1, message = "상태 목록은 비어 있을 수 없습니다.")
    List<@ValidTradeStatus String> status
) {

}
