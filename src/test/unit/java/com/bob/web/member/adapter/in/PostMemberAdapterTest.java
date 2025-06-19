package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostMemberAdapter 테스트")
class PostMemberAdapterTest {

  @Mock
  private MemberReadUseCase readUseCase;

  @InjectMocks
  private PostMemberAdapter postMemberAdapter;

  @Test
  @DisplayName("회원 ID를 통한 게시글 작성자 정보 조회 기능 호출 테스트")
  void 게시글_작성자_정보를_조회한다() {
    // given
    UUID memberId = UUID.randomUUID();
    ReadProfileQuery query = ReadProfileQuery.of(memberId);

    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    PostMemberSummaryResponse result = postMemberAdapter.readPostMemberSummary(memberId);

    // then
    assertThat(result.nickname()).isEqualTo("tester");
    assertThat(result.profileImageUrl()).isEqualTo("http://image.url");
    then(readUseCase).should().readProfileProcess(query);
  }
}