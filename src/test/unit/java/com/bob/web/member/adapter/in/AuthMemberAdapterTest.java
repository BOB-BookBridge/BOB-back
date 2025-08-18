package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import com.bob.domain.member.usecase.MemberWriteUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("AuthMemberAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class AuthMemberAdapterTest {

  @InjectMocks
  private AuthMemberAdapter authMemberAdapter;

  @Mock
  private MemberWriteUseCase writeUseCase;

  @Test
  @DisplayName("소셜 로그인 기능 호출 테스트")
  void 소셜_로그인_메서드_호출() {
    // given
    SocialLoginCommand command = SocialLoginCommand.of("GOOGLE", "test@google.com", "foo");
    given(writeUseCase.socialLoginProcess(command)).willReturn(MEMBER_ID);

    // when
    UUID result = authMemberAdapter.socialLoginProcess("GOOGLE", "test@google.com", "foo");

    // then
    then(writeUseCase).should().socialLoginProcess(command);
    assertThat(result).isEqualTo(MEMBER_ID);
  }
}