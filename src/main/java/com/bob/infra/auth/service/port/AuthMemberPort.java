package com.bob.infra.auth.service.port;

import java.util.UUID;

public interface AuthMemberPort {

  UUID socialLoginProcess(String provider, String email, String nickname);
}
