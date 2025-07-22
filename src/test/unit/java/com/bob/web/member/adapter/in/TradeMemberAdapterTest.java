package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberReadUseCase;
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

  @Test
  @DisplayName("회원 ID를 통한 게시글 작성자 정보 조회 기능 호출 테스트")
  void 게시글_작성자_정보를_조회한다() {
    // given
    ReadProfileQuery query = ReadProfileQuery.of(MEMBER_ID);
    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    MemberProfileResponse response = tradeMemberAdapter.readTradeMemberProfile(MEMBER_ID);

    // then
    assertThat(response.memberId()).isEqualTo(MEMBER_ID);
    assertThat(response.nickname()).isEqualTo("tester");
    then(readUseCase).should().readProfileProcess(query);
  }
}