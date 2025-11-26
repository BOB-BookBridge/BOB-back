package com.bob.core.application.trade.dto.command;

import java.util.List;
import java.util.UUID;

public record ChangeTradeItemsCommand(List<Long> itemIds, UUID memberId) {

}
