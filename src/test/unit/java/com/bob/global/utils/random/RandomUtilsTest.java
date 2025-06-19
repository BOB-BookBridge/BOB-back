package com.bob.global.utils.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@DisplayName("RandomUtils 단위 테스트")
class RandomUtilsTest {

  @Test
  @DisplayName("랜덤 코드 길이 검증 테스트")
  void 요청한_길이의_코드가_생성된다() {
    // given
    int size = 12;

    // when
    String code = RandomUtils.generateCode(size);

    // then
    assertThat(code).hasSize(size);
  }

  @Test
  @DisplayName("코드 검증 테스트")
  void 생성된_코드는_영문자와_숫자만_포함한다() {
    // when
    String code = RandomUtils.generateCode(20);

    // then
    assertThat(code).matches("[A-Za-z0-9]+");
  }

  @RepeatedTest(3)
  @DisplayName("코드 중복 확률 테스트")
  void 같은_길이_요청_시_코드가_매번_다르게_생성된다() {
    int size = 16;
    Set<String> generated = new HashSet<>();

    for (int i = 0; i < 50; i++) {
      generated.add(RandomUtils.generateCode(size));
    }

    assertThat(generated).hasSizeGreaterThan(45);
  }
}