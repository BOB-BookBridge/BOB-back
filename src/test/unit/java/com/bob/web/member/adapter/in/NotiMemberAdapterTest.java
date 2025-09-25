package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberReadUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("NotiMemberAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class NotiMemberAdapterTest {

  @InjectMocks
  private NotiMemberAdapter notiMemberAdapter;

  @Mock
  private MemberReadUseCase readUseCase;

  @Test
  @DisplayName("수신자 프로필 조회 성공 테스트")
  void 수신자_프로필을_정상_조회할_수_있다() {
    // given
    UUID memberId = MEMBER_ID;
    MemberProfileResponse memberSummary = DEFAULT_MEMBER_PROFILE_RESPONSE;
    given(readUseCase.readProfileProcess(ReadProfileQuery.of(memberId, false))).willReturn(memberSummary);

    // when
    MemberProfileResponse actual = notiMemberAdapter.readNotiMemberProfile(memberId);

    // then
    assertThat(actual).isEqualTo(memberSummary);
    verify(readUseCase).readProfileProcess(ReadProfileQuery.of(memberId, false));
  }
}