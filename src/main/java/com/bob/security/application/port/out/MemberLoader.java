package com.bob.security.application.port.out;

import java.util.Optional;

import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.dto.SocialAuthMember;

public interface MemberLoader {

    Optional<AuthMember> load(String email);

    SocialAuthMember load(String provider, String email, String nickname);
}
