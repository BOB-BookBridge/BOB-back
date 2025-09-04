package com.bob.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InterestTest {

  @ParameterizedTest(name = "canonicalize(\"{0}\") => \"{1}\"")
  @MethodSource("canonicalizeCases")
  void canonicalize_테스트(String input, String expected) {
    // when
    String actual = Interest.canonicalize(input);

    // then
    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> canonicalizeCases() {
    return Stream.of(
        Arguments.of("Java", "java"),
        Arguments.of("  Java  ", "java"),
        Arguments.of("  JAVA  SPRING  ", "java spring"),
        Arguments.of("Go\tLang", "go lang"),
        Arguments.of("  KOTLIN\nCOROUTINES  ", "kotlin coroutines"),
        Arguments.of(" sql   server ", "sql server")
    );
  }

  @Test
  void 관심사_생성() {
    // given
    String raw = "  Java   Spring  ";

    // when
    Interest interest = Interest.of(raw);

    // then
    assertThat(interest.getId()).isNull();
    assertThat(interest.getCanonicalName()).isEqualTo("java spring");
  }
}
