package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.response.MemberBooksResponseFixture.DEFAULT_MEMBER_BOOKS_RESPONSE;
import static com.bob.support.fixture.response.MemberBooksResponseFixture.SINGLE_MEMBER_BOOKS_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradeMemberBookAdapter 테스트")
class TradeMemberBookAdapterTest {

  @InjectMocks
  private TradeMemberBookAdapter adapter;

  @Mock
  private MemberBookReadUseCase readUseCase;

  @Mock
  private MemberBookModifyUseCase modifyUseCase;

  @Mock
  private MemberBookRemoveUseCase removeUseCase;


  @Captor
  private ArgumentCaptor<ReadMemberBooksByIdQuery> readIdsCaptor;

  @Captor
  private ArgumentCaptor<AllocateMemberBookUsageCommand> allocateCaptor;

  @Captor
  private ArgumentCaptor<FreeMemberBookUsageByIdsCommand> freeIdsCaptor;

  @Captor
  private ArgumentCaptor<RemoveMemberBooksCommand> removeIdsCaptor;

  @Test
  void 거래_물품_단건_조회() {
    // given
    Long id = 1L;
    given(readUseCase.readMemberBooksByIdsProcess(any(ReadMemberBooksByIdQuery.class))).willReturn(SINGLE_MEMBER_BOOKS_RESPONSE);

    // when
    TradeItemView result = adapter.read(id);

    // then
    then(readUseCase).should(times(1)).readMemberBooksByIdsProcess(readIdsCaptor.capture());
    assertThat(readIdsCaptor.getValue().ids()).containsExactly(id);
    assertThat(result.id()).isEqualTo(id);
  }

  @Test
  void 회원_소유_책_다건_조회시_순서_및_매핑() {
    // given
    List<Long> ids = List.of(1L, 2L);
    given(readUseCase.readMemberBooksByIdsProcess(any(ReadMemberBooksByIdQuery.class))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    // when
    List<TradeItemView> views = adapter.read(ids);

    // then
    then(readUseCase).should(times(1)).readMemberBooksByIdsProcess(readIdsCaptor.capture());
    assertThat(readIdsCaptor.getValue().ids()).containsExactlyElementsOf(ids);

    assertThat(views).hasSize(2);
    TradeItemView v1 = views.get(0);
    TradeItemView v2 = views.get(1);
    assertThat(v1.id()).isEqualTo(1L);
    assertThat(v2.id()).isEqualTo(2L);
  }

  @Test
  void 회원_소유_책_사용처_할당_기능_호출() {
    // given
    List<Long> ids = List.of(1L, 2L, 3L);
    Long usageId = 10L;

    // when
    adapter.allocateUsage(ids, usageId);

    // then
    then(modifyUseCase).should(times(1)).allocateMemberBookUsageProcess(allocateCaptor.capture());

    AllocateMemberBookUsageCommand command = allocateCaptor.getValue();
    assertThat(command.ids()).containsExactly(1L, 2L, 3L);
    assertThat(command.usageId()).isEqualTo(10L);
  }

  @Test
  void 회원_소유_책_사용처_해제_기능_호출() {
    // given
    List<Long> ids = List.of(1L, 2L);

    // when
    adapter.freeUsage(ids);

    // then
    then(modifyUseCase).should(times(1)).freeMemberBookUsageByIdsProcess(freeIdsCaptor.capture());

    FreeMemberBookUsageByIdsCommand command = freeIdsCaptor.getValue();
    assertThat(command.ids()).containsExactly(1L, 2L);
  }

  @Test
  void 회원_소유_책_삭제_기능_호출() {
    // given
    List<Long> memberBookIds = List.of(1L, 2L, 3L);

    // when
    adapter.remove(memberBookIds);

    // then
    then(removeUseCase).should(times(1)).removeMemberBooksProcess(removeIdsCaptor.capture());

    RemoveMemberBooksCommand command = removeIdsCaptor.getValue();
    assertThat(command.ids()).containsExactly(1L, 2L, 3L);
  }
}
