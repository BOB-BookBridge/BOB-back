package com.bob.infra.auth.jwt;

import static com.bob.support.fixture.auth.JWTFixture.SECRET_KEY;
import static com.bob.support.fixture.auth.JWTFixture.expiredToken;
import static com.bob.support.fixture.auth.JWTFixture.validToken;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JWT 유틸 테스트")
class JwtProviderTest {

  private JwtProvider jwtProvider;

  @BeforeEach
  void setUp() {
    jwtProvider = new JwtProvider();
    ReflectionTestUtils.setField(jwtProvider, "key", SECRET_KEY);
    ReflectionTestUtils.setField(jwtProvider, "accessExpireTime", 60L);
  }

  @Test
  @DisplayName("Access Token 생성 - 성공 테스트")
  void AccessToken을_생성할_수_있다() {
    // when
    String token = jwtProvider.generateAccessToken(MEMBER_ID.toString());

    // then
    assertThat(token).isNotBlank();
  }

  @Test
  @DisplayName("MemberId 추출 - 성공 테스트")
  void Token에서_MemberId를_추출할_수_있다() {
    // given
    String token = validToken(MEMBER_ID.toString());

    // when
    UUID memberId = jwtProvider.getMemberId(token);

    // then
    assertThat(memberId).isEqualTo(MEMBER_ID);
  }

  @Test
  @DisplayName("Token 서명 검증 - 성공 테스트")
  void 토큰이_서명되었는지_검증할_수_있다() {
    // given
    String token = validToken(MEMBER_ID.toString());

    // when
    boolean isVerified = jwtProvider.isVerified(token);

    // then
    assertThat(isVerified).isTrue();
  }

  @Test
  @DisplayName("Token 만료 여부 확인 - 성공 테스트(만료되지 않은 경우)")
  void 만료되지_않은_토큰이면_false를_반환한다() {
    // given
    String token = validToken(MEMBER_ID.toString());

    // when
    boolean isExpired = jwtProvider.isExpired(token);

    // then
    assertThat(isExpired).isFalse();
  }

  @Test
  @DisplayName("Token 만료 여부 확인 - 성공 테스트(만료된 경우)")
  void 만료된_토큰이면_true를_반환한다() {
    // given
    String token = expiredToken(MEMBER_ID.toString());

    // when
    boolean isExpired = jwtProvider.isExpired(token);

    // then
    assertThat(isExpired).isTrue();
  }
}
