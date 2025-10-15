package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberBooksResponseFixture.DEFAULT_MEMBER_BOOKS_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradeMemberAdapterTest 테스트")
class TradeMemberAdapterTest {

  @InjectMocks
  private TradeMemberAdapter tradeMemberAdapter;

  @Mock
  private MemberReadUseCase readUseCase;

  @Mock
  private MemberBookReadUseCase bookReadUseCase;

  @Mock
  private MemberBookModifyUseCase bookModifyUseCase;

  @Test
  void 게시글_작성자_정보_조회_기능_호출() {
    // given
    ReadProfileQuery query = ReadProfileQuery.of(MEMBER_ID, false);
    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    MemberProfileResponse response = tradeMemberAdapter.readTradeMemberProfile(MEMBER_ID);

    // then
    assertThat(response.memberId()).isEqualTo(MEMBER_ID);
    assertThat(response.nickname()).isEqualTo("tester");
    then(readUseCase).should().readProfileProcess(query);
  }

  @Test
  void 거래_아이템_정보_조회_기능_호출() {
    // given
    List<Long> ids = List.of(1L, 3L);
    ReadMemberBooksByIdQuery query = ReadMemberBooksByIdQuery.of(ids);
    given(bookReadUseCase.readMemberBooksByIdsProcess(query)).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    // when
    List<TradeItemView> views = tradeMemberAdapter.readTradeItemSummary(ids);

    // then
    assertThat(views).hasSize(2);
    assertThat(views).anySatisfy(v -> assertThat(v.id()).isEqualTo(1L));
    assertThat(views).anySatisfy(v -> assertThat(v.id()).isEqualTo(2L));

    then(bookReadUseCase).should().readMemberBooksByIdsProcess(query);
  }

  @Test
  void 회원_책_사용처_변경_기능_호출() {
    // given
    Long usageId = 5L;
    List<Long> memberBookIds = List.of(10L, 11L);

    // when
    tradeMemberAdapter.changeMemberBookUsage(MEMBER_ID, usageId, memberBookIds, false);

    // then
    then(bookModifyUseCase).should().changeMemberBookUsageProcess(any(ChangeMemberBookUsageCommand.class));
  }
}