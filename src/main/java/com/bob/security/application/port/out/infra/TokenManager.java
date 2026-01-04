package com.bob.security.application.port.out.infra;

import java.util.Map;

public interface TokenManager {

    String create(Map<String, String> claims);

    String create(Map<String, String> claims, Long expiration);

    Map<String, String> getClaims(String token);

    boolean verify(String token);

    boolean expire(String token);
}
