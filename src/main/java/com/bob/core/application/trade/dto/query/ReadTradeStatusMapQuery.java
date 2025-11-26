package com.bob.core.application.trade.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadTradeStatusMapQuery(UUID memberId, List<Long> postIds) {

}
