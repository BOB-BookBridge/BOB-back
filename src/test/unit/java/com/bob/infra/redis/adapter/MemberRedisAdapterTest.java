package com.bob.infra.redis.adapter;

import static com.bob.global.exception.response.ApplicationError.UNVERIFIED_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.infra.redis.adapter.in.MemberRedisAdapter;
import com.bob.infra.redis.repository.RedisRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("회원 서비스용 REDIS 접근 테스트")
@ExtendWith(MockitoExtension.class)
public class MemberRedisAdapterTest {

  @InjectMocks
  private MemberRedisAdapter redisAdapter;

  @Mock
  private RedisRepository redisRepository;

  @Test
  @DisplayName("인증 상태 조회 - 성공 테스트")
  void 성공한_이메일_인증상태를_조회한다() {
    // given
    String email = "user@email.com";
    given(redisRepository.getValue(emailVerifiedKey(email))).willReturn(Optional.of("true"));

    // when
    boolean result = redisAdapter.isVerified(email);

    // then
    assertThat(result).isEqualTo(true);
  }

  @Test
  @DisplayName("인증 상태 조회 - 실패 테스트")
  void 존재하지_않는_이메일_인증상태를_조회한다() {
    // given
    String email = "user@email.com";
    given(redisRepository.getValue(emailVerifiedKey(email))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> redisAdapter.isVerified(email))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(UNVERIFIED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("인증 상태 삭제 테스트")
  void 이메일_인증상태를_삭제한다() {
    // given
    String email = "user@email.com";

    // when
    redisAdapter.deleteVerified(email);

    // then
    then(redisRepository).should().delete(emailVerifiedKey(email));
  }

  private String emailVerifiedKey(String email) {
    return "email-verified:" + email;
  }
}
