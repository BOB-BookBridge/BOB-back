package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.trade.service.port.out.TradeMemberBookPort;
import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradeMemberBookAdapter implements TradeMemberBookPort {

  private final MemberBookReadUseCase readUseCase;
  private final MemberBookModifyUseCase modifyUseCase;
  private final MemberBookRemoveUseCase removeUseCase;

  @Override
  public TradeItemView read(Long id) {
    ReadMemberBooksByIdQuery query = ReadMemberBooksByIdQuery.of(List.of(id));
    MemberBooksResponse response = readUseCase.readMemberBooksByIdsProcess(query);
    MemberBookSummary book = response.bookcase().get(0);
    return TradeItemView.of(
        book.id(),
        book.status(),
        book.title(),
        book.author(),
        book.priceStandard(),
        book.cover(),
        book.pubDate(),
        book.available()
    );
  }

  @Override
  public List<TradeItemView> read(List<Long> ids) {
    ReadMemberBooksByIdQuery query = ReadMemberBooksByIdQuery.of(ids);
    MemberBooksResponse response = readUseCase.readMemberBooksByIdsProcess(query);
    return response.bookcase().stream()
        .map(book -> TradeItemView.of(
            book.id(),
            book.status(),
            book.title(),
            book.author(),
            book.priceStandard(),
            book.cover(),
            book.pubDate(),
            book.available()
        ))
        .toList();
  }

  @Override
  public void allocateUsage(List<Long> ids, Long usageId) {
    AllocateMemberBookUsageCommand command = AllocateMemberBookUsageCommand.of(ids, usageId);
    modifyUseCase.allocateMemberBookUsageProcess(command);
  }

  @Override
  public void freeUsage(List<Long> ids) {
    FreeMemberBookUsageByIdsCommand command = FreeMemberBookUsageByIdsCommand.of(ids);
    modifyUseCase.freeMemberBookUsageByIdsProcess(command);
  }

  @Override
  public void remove(List<Long> memberBookIds) {
    RemoveMemberBooksCommand command = RemoveMemberBooksCommand.of(memberBookIds);
    removeUseCase.removeMemberBooksProcess(command);
  }
}
