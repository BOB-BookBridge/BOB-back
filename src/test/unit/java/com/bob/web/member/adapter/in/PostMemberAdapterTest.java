package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
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

  @InjectMocks
  private PostMemberAdapter postMemberAdapter;

  @Mock
  private MemberReadUseCase readUseCase;

  @Mock
  private MemberBookWriteUseCase bookWriteUseCase;

  @Test
  @DisplayName("회원 ID를 통한 게시글 작성자 정보 조회 기능 호출 테스트")
  void 게시글_작성자_정보를_조회한다() {
    // given
    UUID memberId = UUID.randomUUID();
    ReadProfileQuery query = ReadProfileQuery.of(memberId, false);

    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    MemberProfileResponse response = postMemberAdapter.readPostMemberSummary(memberId);

    // then
    assertThat(response.nickname()).isEqualTo("tester");
    assertThat(response.profileImageUrl()).isEqualTo("http://image.url");
    then(readUseCase).should().readProfileProcess(query);
  }

  @Test
  void 게시글_작성_시_사용된_책은_회원의_소유_책으로_등록한다() {
    // given
    RegisterMemberBookCommand command = DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;

    // when
    postMemberAdapter.createMemberBook(command);

    // then
    then(bookWriteUseCase).should(times(1)).registerMemberBookProcess(command);
  }
}