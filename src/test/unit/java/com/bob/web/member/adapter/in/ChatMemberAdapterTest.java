package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

@DisplayName("ChatMember Adapter 테스트")
@ExtendWith(MockitoExtension.class)
class ChatMemberAdapterTest {

  @InjectMocks
  private ChatMemberAdapter chatMemberAdapter;

  @Mock
  private MemberReadUseCase readUseCase;

  @Test
  @DisplayName("채팅 멤버 프로필 조회 테스트")
  void 채팅_상대방의_memberId로_프로필을_조회한다() {
    // given
    UUID memberId = MEMBER_ID;
    ReadProfileQuery query = ReadProfileQuery.of(memberId, false);
    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    MemberProfileResponse result = chatMemberAdapter.readChatMemberProfile(memberId);

    // then
    then(readUseCase).should().readProfileProcess(query);
    assertThat(result.memberId()).isEqualTo(MEMBER_ID);
  }
}