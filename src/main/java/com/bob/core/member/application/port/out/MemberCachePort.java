package com.bob.core.member.application.port.out;

import java.util.Optional;

public interface MemberCachePort {

    void setAuthenticationCode(String email, String code);

    void setAuthenticationSuccess(String email);

    boolean checkAuthenticationSuccess(String email);

    Optional<String> get(String email);

    void delete(String email);
}
