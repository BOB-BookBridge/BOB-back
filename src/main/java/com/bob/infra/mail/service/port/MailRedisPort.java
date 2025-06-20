package com.bob.infra.mail.service.port;

import java.util.Optional;

public interface MailRedisPort {

  void saveVerified(String email, String value, int expireMinutes);

  void saveCode(String email, String code, int expireMinutes);

  Optional<String> getCode(String email);

  void deleteCode(String email);
}
