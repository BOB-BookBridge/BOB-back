package com.bob.infrastructure.secure.adapter.jwt;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("토큰 제공자 테스트")
class JwtTokenManagerTest {

    private final JwtTokenManager jwtTokenManager = new JwtTokenManager();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenManager, "key", "verysecretverysecretverysecretve");
        ReflectionTestUtils.setField(jwtTokenManager, "accessExpireTime", 7200L);
    }

    @Test
    void 토큰_생성() {
        Map<String, String> claims = Map.of(
            "memberId", MEMBER_ID.toString(),
            "role", "USER"
        );

        String token = jwtTokenManager.create(claims);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 토큰_claim_추출() {
        Map<String, String> claims = Map.of(
            "memberId", MEMBER_ID.toString(),
            "role", "USER"
        );
        String token = jwtTokenManager.create(claims);

        Map<String, String> extractedClaims = jwtTokenManager.getClaims(token);

        assertThat(extractedClaims.get("memberId")).isEqualTo(MEMBER_ID.toString());
        assertThat(extractedClaims.get("role")).isEqualTo("USER");
    }

    @Test
    void 토큰_서명_검증() {
        Map<String, String> claims = Map.of("memberId", MEMBER_ID.toString());
        String token = jwtTokenManager.create(claims);

        boolean isVerified = jwtTokenManager.verify(token);

        assertThat(isVerified).isTrue();
    }

    @Test
    void 토큰_서명_검증_시_조작된_토큰이면_false_반환() {
        Map<String, String> claims = Map.of("memberId", MEMBER_ID.toString());
        String invalidSignToken = jwtTokenManager.create(claims) + "invalid";

        boolean verified = jwtTokenManager.verify(invalidSignToken);

        assertThat(verified).isFalse();
    }

    @Test
    void 토큰_만료_확인() {
        Map<String, String> claims = Map.of("memberId", MEMBER_ID.toString());
        String token = jwtTokenManager.create(claims);

        boolean expired = jwtTokenManager.expire(token);

        assertThat(expired).isFalse();
    }

    @Test
    void 토큰_만료_확인_시_만료된_토큰이면_true_반환() {
        Map<String, String> claims = Map.of("memberId", MEMBER_ID.toString());
        String token = jwtTokenManager.create(claims, -1L);

        boolean expired = jwtTokenManager.expire(token);

        assertThat(expired).isTrue();
    }

    @Test
    void 토큰_만료_확인_시_조작된_토큰이더라도_true_반환() {
        Map<String, String> claims = Map.of("memberId", MEMBER_ID.toString());
        String invalidSignToken = jwtTokenManager.create(claims) + "invalid";

        boolean expired = jwtTokenManager.expire(invalidSignToken);

        assertThat(expired).isTrue();
    }
}
