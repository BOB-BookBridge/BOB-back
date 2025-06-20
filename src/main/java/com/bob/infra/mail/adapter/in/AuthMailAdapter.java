package com.bob.infra.mail.adapter.in;

import com.bob.infra.mail.service.GoogleMailService;
import com.bob.web.auth.mail.port.out.AuthMailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthMailAdapter implements AuthMailPort {

  private final GoogleMailService mailService;

  @Override
  public void sendCode(String email) {
    mailService.sendCodeProcess(email);
  }

  @Override
  public void verifyCode(String email, String code) {
    mailService.verifyCodeProcess(email, code);
  }
}
