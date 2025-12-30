package com.bob.core.trade.adapter.api.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

import com.bob.shared.web.annotation.AllowedValues;

public record ReadTradesRequest(
    @AllowedValues(
        value = {"ALL", "SENT", "RECEIVED"},
        ignoreCase = true
    )
    String key,

    @Nullable
    @Size(min = 1, message = "상태 목록은 비어 있을 수 없습니다.")
    List<@AllowedValues(
        value = {"ALL", "CANCELED", "REQUESTED", "ACCEPTED", "RESERVED", "COMPLETED", "REJECTED"},
        ignoreCase = true,
        allowNull = false
    ) String> status
) {

}
