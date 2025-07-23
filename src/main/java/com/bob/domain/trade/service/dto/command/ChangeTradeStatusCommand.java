package com.bob.domain.trade.service.dto.command;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ChangeTradeStatusCommand(
    UUID memberId,
    Long tradeId,
    String status
) {

}
