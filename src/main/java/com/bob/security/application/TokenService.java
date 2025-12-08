package com.bob.security.application;

import static com.bob.global.exception.response.AuthenticationError.AUTHENTICATION_FAILED;
import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.addCookie;
import static com.bob.global.utils.web.CookieUtils.getCookie;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.in.TokenIssuer;
import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.MemberLoader;
import com.bob.security.application.port.out.TokenManager;

@Service
@RequiredArgsConstructor
public class TokenService implements TokenIssuer {

    private final TokenManager tokenManager;

    private final AuthCachePort cachePort;

    private final MemberLoader memberLoader;

    @Value("${jwt.access-name}")
    private String accessName;

    @Value("${jwt.refresh-name}")
    private String refreshName;

    @Value("${jwt.refresh-token-expire-time}")
    private Long refreshExpireTime;

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String old = getCookie(request, refreshName);
        String current = generateCode(32);

        String id = getId(old);
        cachePort.updateRefreshKey(old, current, id);

        AuthMember member = memberLoader.load(UUID.fromString(id));

        String accessToken = tokenManager.create(Map.of("memberId", id, "role", member.role()));
        addCookie(response, accessName, accessToken, refreshExpireTime);
        addCookie(response, refreshName, current, refreshExpireTime);
    }

    private String getId(String key) {
        verifyLoggedIn(key);

        return cachePort.get(key)
            .orElseThrow(() -> new ApplicationAuthenticationException(AUTHENTICATION_FAILED));
    }

    private static void verifyLoggedIn(String key) {
        if (key == null)
            throw new ApplicationAuthenticationException(AUTHENTICATION_FAILED);
    }
}
