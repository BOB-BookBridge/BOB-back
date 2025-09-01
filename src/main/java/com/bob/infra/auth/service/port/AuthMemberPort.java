package com.bob.infra.auth.service.port;

import com.bob.domain.member.service.dto.response.SocialLoginResponse;

public interface AuthMemberPort {

  SocialLoginResponse socialLoginProcess(String provider, String email, String nickname);
}
