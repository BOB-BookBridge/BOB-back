package com.bob.domain.trade.service.port.out;

import java.util.List;

public interface TradeMemberBookPort {

  void allocateUsage(List<Long> ids, Long usageId);

  void freeUsage(List<Long> ids);

  void remove(List<Long> ids);

  // void allocateUsageWithAuth(List<Long> ids, Long usageId, UUID memberId);
  // void freeUsageWithAuth(List<Long> ids, UUID memberId);
}
