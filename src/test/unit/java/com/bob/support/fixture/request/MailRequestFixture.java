package com.bob.support.fixture.request;

import com.bob.web.auth.mail.request.MailSendRequest;
import com.bob.web.auth.mail.request.MailVerifyRequest;

public class MailRequestFixture {

  public static final MailSendRequest MAIL_SEND_REQUEST = new MailSendRequest("test@email.com");
  public static final MailVerifyRequest MAIL_VERIFY_REQUEST = new MailVerifyRequest("test@email.com", "ABC123");
}