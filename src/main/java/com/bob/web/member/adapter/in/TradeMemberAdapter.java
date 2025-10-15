package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradeMemberAdapter implements TradeMemberPort {

  private final MemberReadUseCase readUseCase;

  private final MemberBookReadUseCase bookReadUseCase;
  private final MemberBookModifyUseCase bookModifyUseCase;

  @Override
  public MemberProfileResponse readTradeMemberProfile(UUID memberId) {
    return readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false));
  }

  @Override
  public List<TradeItemView> readTradeItemSummary(List<Long> ids) {
    MemberBooksResponse response = bookReadUseCase.readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(ids));
    if (response == null || response.books() == null)
      return List.of();

    return response.books().stream()
        .map(b -> TradeItemView.of(
            b.id(), b.status(), b.title(), b.author(),
            b.priceStandard(), b.cover(), b.pubDate()
        ))
        .toList();
  }

  @Override
  public void changeMemberBookUsage(UUID memberId, Long usageId, List<Long> memberBookIds, boolean release) {
    ChangeMemberBookUsageCommand command = ChangeMemberBookUsageCommand.of(memberId, usageId, memberBookIds, release);
    bookModifyUseCase.changeMemberBookUsageProcess(command);
  }
}
