package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import com.bob.domain.trade.service.port.out.TradeMemberWishPort;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradeMemberWishAdapter implements TradeMemberWishPort {

  private final MemberWishReadUseCase readUseCase;
  private final MemberBookReadUseCase bookReadUseCase;

  @Override
  public boolean allMatch(UUID memberId, List<Long> ids) {
    List<Long> wishBookIds = getWishBookIds(memberId);
    List<Long> requesterBookIds = getRequesterBookIds(ids);
    return new HashSet<>(wishBookIds).containsAll(requesterBookIds);
  }

  private List<Long> getWishBookIds(UUID memberId) {
    MemberWishesResult result = readUseCase.readWishesProcess(ReadMemberWishesQuery.of(memberId));
    return result.wishes().stream()
        .map(MemberWishSummary::bookId)
        .toList();
  }

  private List<Long> getRequesterBookIds(List<Long> ids) {
    MemberBooksResponse response = bookReadUseCase.readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(ids));
    return response.bookcase().stream()
        .map(MemberBookSummary::bookId)
        .toList();
  }
}
