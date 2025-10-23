package com.bob.domain.trade.service.port.out;

import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;

public interface TradeMemberBookPort {

  TradeItemView read(Long id);

  List<TradeItemView> read(List<Long> ids);

  void allocateUsage(List<Long> ids, Long usageId);

  void freeUsage(List<Long> ids);

  void remove(List<Long> ids);

  // void allocateUsageWithAuth(List<Long> ids, Long usageId, UUID memberId);
  // void freeUsageWithAuth(List<Long> ids, UUID memberId);
}
