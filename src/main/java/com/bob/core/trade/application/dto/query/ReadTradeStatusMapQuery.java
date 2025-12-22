package com.bob.core.trade.application.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadTradeStatusMapQuery(UUID memberId, List<Long> postIds) {

}
