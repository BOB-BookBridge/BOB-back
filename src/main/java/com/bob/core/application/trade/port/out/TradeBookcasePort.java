package com.bob.core.application.trade.port.out;

import java.util.List;
import java.util.UUID;

import com.bob.core.application.trade.port.result.TradeBookcaseItem;

public interface TradeBookcasePort {

    TradeBookcaseItem read(Long id);

    List<TradeBookcaseItem> read(List<Long> ids);

    void allocateUsageWithAuth(List<Long> ids, UUID memberId, Long usageId);

    void freeUsage(List<Long> ids);

    void freeUsageWithAuth(List<Long> ids, UUID memberId);

    void delete(List<Long> ids);
}
