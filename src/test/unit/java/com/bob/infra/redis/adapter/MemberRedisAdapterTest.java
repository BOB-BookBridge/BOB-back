package com.bob.infra.redis.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
  @DisplayName("인증 상태 조회 테스트")
  void 이메일_인증상태를_조회한다() {
    // given
    String email = "user@email.com";
    given(redisRepository.getValue(emailVerifiedKey(email))).willReturn(Optional.of("true"));

    // when
    boolean result = redisAdapter.isVerified(email);

    // then
    assertThat(result).isEqualTo(true);
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
