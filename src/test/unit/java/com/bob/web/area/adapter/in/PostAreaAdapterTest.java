package com.bob.web.area.adapter.in;

import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.AreaSummaryResponseFixture.DEFAULT_AREA_SUMMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.area.usecase.AreaReadUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostAreaAdapter 테스트")
class PostAreaAdapterTest {

  @InjectMocks
  private PostAreaAdapter postAreaAdapter;

  @Mock
  private AreaReadUseCase readUseCase;

  @Test
  @DisplayName("회원 ID를 통한 게시글 지역 정보 조회 테스트")
  void 게시글_지역_요약정보를_조회한다() {
    // given
    UUID memberId = MEMBER_ID;
    ReadAreaQuery query = ReadAreaQuery.of(memberId);

    given(readUseCase.readAreaSummaryProcess(query)).willReturn(DEFAULT_AREA_SUMMARY);

    // when
    AreaSummaryResponse response = postAreaAdapter.readPostAreaSummary(memberId);

    // then
    assertThat(response.emdId()).isEqualTo(EMD_AREA_ID);
    assertThat(response.emdName()).isEqualTo("역삼동");
    assertThat(response.siggName()).isEqualTo("강남구");
    assertThat(response.validity()).isTrue();

    verify(readUseCase).readAreaSummaryProcess(query);
  }
}
