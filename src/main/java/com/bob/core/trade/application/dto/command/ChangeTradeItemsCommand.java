package com.bob.core.trade.application.dto.command;

import java.util.List;
import java.util.UUID;

public record ChangeTradeItemsCommand(List<Long> itemIds, UUID memberId) {

}
