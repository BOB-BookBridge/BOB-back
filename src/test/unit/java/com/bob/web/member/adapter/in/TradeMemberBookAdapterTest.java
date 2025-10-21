package com.bob.web.member.adapter.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
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
  private MemberBookModifyUseCase modifyUseCase;

  @Mock
  private MemberBookRemoveUseCase removeUseCase;

  @Captor
  private ArgumentCaptor<AllocateMemberBookUsageCommand> allocateCaptor;

  @Captor
  private ArgumentCaptor<FreeMemberBookUsageByIdsCommand> freeIdsCaptor;

  @Captor
  private ArgumentCaptor<RemoveMemberBooksCommand> removeIdsCaptor;

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
