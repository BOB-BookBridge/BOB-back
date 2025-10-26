package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberBooksResponseFixture.CUSTOM_MEMBER_BOOKS_RESPONSE;
import static com.bob.support.fixture.response.MemberWishesResultFixture.CUSTOM_MEMBER_WISHES_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("TradeMemberWishAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class TradeMemberWishAdapterTest {

  @InjectMocks
  private TradeMemberWishAdapter adapter;

  @Mock
  private MemberWishReadUseCase readUseCase;

  @Mock
  private MemberBookReadUseCase bookReadUseCase;

  @Test
  void 거래_요청_물품이_희망_도서와_일치() {
    // given
    UUID memberId = MEMBER_ID;
    List<Long> requesterTradeItemIds = List.of(2L);
    // 판매자 희망 책 id = 6
    given(readUseCase.readWishesProcess(ReadMemberWishesQuery.of(memberId))).willReturn(CUSTOM_MEMBER_WISHES_RESULT(3L, 6L));
    // 요청 거래 물품 Id: 10 -> 책 ID: 6
    given(bookReadUseCase.readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(requesterTradeItemIds))).willReturn(CUSTOM_MEMBER_BOOKS_RESPONSE(10L, 6L));

    // when
    boolean allMatch = adapter.allMatch(memberId, requesterTradeItemIds);

    // then
    assertThat(allMatch).isTrue();
    then(readUseCase).should().readWishesProcess(ReadMemberWishesQuery.of(memberId));
    then(bookReadUseCase).should().readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(requesterTradeItemIds));
  }

  @Test
  void 거래_요청_물품이_희망_도서와_불일치() {
    // given
    UUID memberId = MEMBER_ID;
    List<Long> requesterIds = List.of(2L);
    // 판매자 희망 책 id = 6
    given(readUseCase.readWishesProcess(ReadMemberWishesQuery.of(memberId))).willReturn(CUSTOM_MEMBER_WISHES_RESULT(6L, 6L));
    // 요청 거래 물품 Id: 10 -> 책 ID: 7
    given(bookReadUseCase.readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(requesterIds))).willReturn(CUSTOM_MEMBER_BOOKS_RESPONSE(10L, 7L));

    // when
    boolean allMatch = adapter.allMatch(memberId, requesterIds);

    // then
    assertThat(allMatch).isFalse();
    then(readUseCase).should().readWishesProcess(ReadMemberWishesQuery.of(memberId));
    then(bookReadUseCase).should().readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery.of(requesterIds));
  }
}
