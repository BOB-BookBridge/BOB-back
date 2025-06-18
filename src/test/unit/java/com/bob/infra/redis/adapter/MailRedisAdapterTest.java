package com.bob.infra.redis.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.infra.redis.repository.RedisRepository;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메일 서비스용 REDIS 접근 테스트")
@ExtendWith(MockitoExtension.class)
public class MailRedisAdapterTest {

  @InjectMocks
  private MailRedisAdapter redisAdapter;

  @Mock
  private RedisRepository redisRepository;

  @Test
  @DisplayName("인증 상태 저장 테스트")
  void 이메일_인증상태를_저장한다() {
    // given
    String email = "user@email.com";
    String value = "true";
    int minutes = 3;

    // when
    redisAdapter.saveVerified(email, value, minutes);

    // then
    then(redisRepository).should().setValue(emailVerifiedKey(email), value, Duration.ofMinutes(minutes));
  }

  @Test
  @DisplayName("인증 코드 저장 테스트")
  void 인증코드를_저장한다() {
    // given
    String email = "user@email.com";
    String code = "123456";
    int minutes = 3;

    // when
    redisAdapter.saveCode(email, code, minutes);

    // then
    then(redisRepository).should().setValue(emailCodeKey(email), code, Duration.ofMinutes(minutes));
    ;
  }

  @Test
  @DisplayName("인증 코드 조회 테스트")
  void 인증코드를_조회한다() {
    // given
    String email = "user@email.com";
    given(redisRepository.getValue(emailCodeKey(email))).willReturn(Optional.of("123456"));

    // when
    Optional<String> result = redisAdapter.getCode(email);

    // then
    assertThat(result).isPresent().contains("123456");
  }

  @Test
  @DisplayName("인증 코드 삭제 테스트")
  void 인증코드를_삭제한다() {
    // given
    String email = "user@email.com";

    // when
    redisAdapter.deleteCode(email);

    // then
    then(redisRepository).should().delete(emailCodeKey(email));
  }

  private String emailCodeKey(String email) {
    return "email-code:" + email;
  }

  private String emailVerifiedKey(String email) {
    return "email-verified:" + email;
  }
}
