package com.bob.web.auth.mail.port.out;

public interface AuthMailPort {

  void sendCode(String email);

  void verifyCode(String email, String code);
}
