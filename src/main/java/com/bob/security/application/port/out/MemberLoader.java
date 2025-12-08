package com.bob.security.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.dto.SocialAuthMember;

public interface MemberLoader {

    Optional<AuthMember> load(String email);

    AuthMember load(UUID id);

    SocialAuthMember load(String provider, String email, String nickname);
}
