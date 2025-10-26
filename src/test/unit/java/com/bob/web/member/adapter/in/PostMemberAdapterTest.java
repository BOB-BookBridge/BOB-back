package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByUsageIdCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadProfileQuery;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.post.service.port.view.PostMemberView;
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

  @Mock
  private MemberBookModifyUseCase bookModifyUseCase;

  @Test
  void 게시글_작성자_정보_조회_기능_호출() {
    // given
    UUID memberId = UUID.randomUUID();
    ReadProfileQuery query = ReadProfileQuery.of(memberId, false);

    given(readUseCase.readProfileProcess(query)).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    PostMemberView view = postMemberAdapter.readPostMemberSummary(memberId);

    // then
    assertThat(view.nickname()).isEqualTo("tester");
    assertThat(view.profileUrl()).isEqualTo("http://image.url");
    assertThat(view.interests()).isNotNull();
    assertThat(view.wishes()).isNotNull();
    then(readUseCase).should().readProfileProcess(query);
  }

  @Test
  void 회원_소유_책_등록_기능_호출() {
    // given
    RegisterMemberBookCommand command = DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;

    // when
    postMemberAdapter.createMemberBook(command);

    // then
    then(bookWriteUseCase).should(times(1)).registerMemberBookProcess(command);
  }

  @Test
  void 회원_소유_책_사용처_업데이트_기능_호출() {
    // given
    Long usageId = 1L;
    Long memberBookId = 1L;

    // when
    postMemberAdapter.changeMemberBookUsage(MEMBER_ID, usageId, memberBookId);

    // then
    then(bookModifyUseCase).should(times(1)).changeMemberBookUsageProcess(any(ChangeMemberBookUsageCommand.class));
  }

  @Test
  void 회원_소유_책_사용처_삭제_기능_호출() {
    // given
    Long usageId = 1L;

    // when
    postMemberAdapter.removeMemberBookUsage(usageId);

    // then
    then(bookModifyUseCase).should(times(1)).freeMemberBookUsageByUsageIdProcess(any(FreeMemberBookUsageByUsageIdCommand.class));
  }
}