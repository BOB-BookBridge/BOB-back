package com.bob.web.auth.mail;

import static com.bob.support.fixture.request.MailRequestFixture.MAIL_SEND_REQUEST;
import static com.bob.support.fixture.request.MailRequestFixture.MAIL_VERIFY_REQUEST;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.web.auth.mail.controller.MailController;
import com.bob.web.auth.mail.port.out.AuthMailPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("이메일 API 테스트")
@ExtendWith(MockitoExtension.class)
class MailControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @InjectMocks
  private MailController mailController;
  @Mock
  private AuthMailPort mailPort;

  @Test
  @DisplayName("이메일 인증 코드 전송 요청 테스트")
  void 이메일_인증_코드_전송_API를_호출할_수_있다() throws Exception {
    MockMvc mvc = standaloneSetup(mailController).build();

    mvc.perform(post("/auth/email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(MAIL_SEND_REQUEST)))
        .andExpect(status().isOk());

    verify(mailPort, times(1)).sendCode(MAIL_SEND_REQUEST.email());
  }

  @Test
  @DisplayName("이메일 인증 코드 검증 요청 테스트")
  void 이메일_인증_코드_검증_API를_호출할_수_있다() throws Exception {
    MockMvc mvc = standaloneSetup(mailController).build();

    mvc.perform(post("/auth/email/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(MAIL_VERIFY_REQUEST)))
        .andExpect(status().isOk());

    verify(mailPort, times(1)).verifyCode(MAIL_VERIFY_REQUEST.email(), MAIL_VERIFY_REQUEST.code());
  }
}
