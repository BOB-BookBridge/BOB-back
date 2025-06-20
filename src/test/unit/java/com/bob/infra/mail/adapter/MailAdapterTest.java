package com.bob.infra.mail.adapter;

import static com.bob.support.fixture.auth.MailFixture.EMAIL;
import static org.mockito.BDDMockito.then;

import com.bob.infra.mail.adapter.in.MemberMailAdapter;
import com.bob.infra.mail.service.GoogleMailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Email Adapter 테스트")
@ExtendWith(MockitoExtension.class)
public class MailAdapterTest {

  @InjectMocks
  private MemberMailAdapter mailAdapter;

  @Mock
  private GoogleMailService googleMailService;

  @Test
  @DisplayName("임시 비밀번호 전송 테스트")
  void 사용자의_이메일로_임시_비밀번호를_전송한다() {
    // given
    String tempPassword = "tempPassword";
    String email = EMAIL;

    // when
    mailAdapter.sendTempPassword(email, tempPassword);

    // then
    then(googleMailService).should().sendTempPasswordProcess(email, tempPassword);
  }
}
