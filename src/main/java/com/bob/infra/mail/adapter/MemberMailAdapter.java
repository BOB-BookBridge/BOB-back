package com.bob.infra.mail.adapter;

import com.bob.domain.member.service.port.MemberMailPort;
import com.bob.infra.mail.service.GoogleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMailAdapter implements MemberMailPort {

  private final GoogleMailService mailService;

  @Override
  public void sendTempPasswordProcess(String email, String tempPassword) {
    mailService.sendMail("임시 비밀번호", tempPassword, email);
  }
}
