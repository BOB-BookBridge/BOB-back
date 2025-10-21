package com.bob.domain.trade.service.port.out;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;
import java.util.UUID;

public interface TradeMemberPort {

  // TODO : port 내부 DTO 사용으로 변경 ex) MemberProfileView, 변환은 adapter에서 수행
  MemberProfileResponse readTradeMemberProfile(UUID memberId);

  // TODO : 책장 read, change TradeMemberBookPort 사용 전환
  List<TradeItemView> readTradeItemSummary(List<Long> ids);

  void changeMemberBookUsage(UUID memberId, Long usageId, List<Long> memberBookIds, boolean release);
}
