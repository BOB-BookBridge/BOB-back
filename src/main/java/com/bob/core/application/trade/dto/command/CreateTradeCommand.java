package com.bob.core.application.trade.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateTradeCommand(Long postId, UUID buyerId, List<Long> itemIds, boolean isFar) {

}
