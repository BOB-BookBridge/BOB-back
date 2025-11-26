package com.bob.security.application.port.out;

import java.util.UUID;

/* FUTURE : 여러 개의 claim 포함 시 map 파라미터 활용 및 조회 */
public interface TokenManager {

    String create(String claim);

    String create(String claim, Long expireTime);

    UUID getClaim(String token);

    boolean verify(String token);

    boolean expire(String token);
}
