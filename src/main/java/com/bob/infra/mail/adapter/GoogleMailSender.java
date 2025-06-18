package com.bob.infra.mail.adapter;

import static com.bob.global.exception.response.ApplicationError.EXPIRED_MAIL_CODE;
import static com.bob.global.exception.response.ApplicationError.INVALID_MAIL_CODE;

import com.bob.domain.member.service.port.MemberMailPort;
import com.bob.domain.member.service.port.MemberRedisPort;
import com.bob.global.exception.exceptions.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GoogleMailSender implements MemberMailPort {

  private final JavaMailSender mailSender;
  private final SimpleMailMessage message;
  private final MemberRedisPort mailVerificationStore;

  @Override
  public void sendCodeProcess(String email) {
    String verificationCode = generateCode(6);
    mailVerificationStore.saveCode(email, verificationCode, 3);
    sendMail("인증 코드", verificationCode, email);
  }

  @Override
  public void verifyCodeProcess(String email, String code) {
    String storedCode = mailVerificationStore.getCode(email)
        .orElseThrow(() -> new ApplicationException(EXPIRED_MAIL_CODE));

    if (!storedCode.equals(code)) {
      throw new ApplicationException(INVALID_MAIL_CODE);
    }

    mailVerificationStore.saveVerified(email, "true", 10);
    mailVerificationStore.deleteCode(email);
  }

  @Override
  public String sendTempPasswordProcess(String email) {
    String tempPassword = generateCode(12);
    sendMail("임시 비밀번호", tempPassword, email);
    return tempPassword;
  }

  private void sendMail(String title, String content, String email) {
    message.setSubject("[BookBridge] " + title + " 안내");
    message.setText(title + " : " + content);
    message.setTo(email);
    mailSender.send(message);
  }
}
