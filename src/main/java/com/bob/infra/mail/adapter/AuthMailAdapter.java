package com.bob.infra.mail.adapter;

import com.bob.infra.mail.service.GoogleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthMailAdapter {

  private final GoogleMailService mailService;

  public void sendCode(String email) {
    mailService.sendCodeProcess(email);
  }

  public void verifyCode(String email, String code) {
    mailService.verifyCodeProcess(email, code);
  }
}
