package com.bob.web.area.adapter.in;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.area.service.dto.command.CreateAreaCommand;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.usecase.AreaReadUseCase;
import com.bob.domain.area.usecase.AreaWriteUseCase;
import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberAreaAdapter 테스트")
class MemberAreaAdapterTest {

  @InjectMocks
  private MemberAreaAdapter memberAreaAdapter;

  @Mock
  private AreaWriteUseCase writeUseCase;

  @Mock
  private AreaReadUseCase readUseCase;

  @Test
  @DisplayName("회원 활동 지역 생성 기능 호출 테스트")
  void 회원_활동_지역을_생성한다() {
    // given
    UUID memberId = MEMBER_ID;
    Integer emdId = EMD_AREA_ID;
    CreateAreaCommand command = CreateAreaCommand.of(memberId, emdId);

    // when
    memberAreaAdapter.createMemberActivityArea(memberId, emdId);

    // then
    then(writeUseCase).should().createActivityAreaProcess(command);
  }

  @Test
  @DisplayName("회원 ID를 통한 활동 지역 정보 조회 기능 호출 테스트")
  void 회원_활동_지역_요약정보를_조회한다() {
    // given
    UUID memberId = MEMBER_ID;
    ReadAreaQuery query = ReadAreaQuery.of(memberId);

    given(readUseCase.readAreaSummaryProcess(query)).willReturn(DEFAULT_AREA_SUMMARY);

    // when
    MemberAreaSummaryResponse result = memberAreaAdapter.readMemberAreaSummary(memberId);

    // then
    assertThat(result.emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(result.validity()).isTrue();
    assertThat(result.authenticatedAt()).isEqualTo(LocalDate.now());
  }
}