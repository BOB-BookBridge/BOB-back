package com.bob.infra.mail.adapter.in;

import static org.mockito.BDDMockito.then;

import com.bob.infra.mail.service.GoogleMailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthMailAdapter 테스트")
class AuthMailAdapterTest {

  @InjectMocks
  private AuthMailAdapter authMailAdapter;

  @Mock
  private GoogleMailService mailService;

  @Test
  @DisplayName("이메일 코드 전송 기능 호출 테스트")
  void 이메일_코드_전송을_위임한다() {
    // given
    String email = "test@example.com";

    // when
    authMailAdapter.sendCode(email);

    // then
    then(mailService).should().sendCodeProcess(email);
  }

  @Test
  @DisplayName("이메일 코드 검증 기능 호출 테스트")
  void 이메일_코드_검증을_위임한다() {
    // given
    String email = "test@example.com";
    String code = "123456";

    // when
    authMailAdapter.verifyCode(email, code);

    // then
    then(mailService).should().verifyCodeProcess(email, code);
  }
}