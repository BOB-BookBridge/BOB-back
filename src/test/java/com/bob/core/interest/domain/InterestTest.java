package com.bob.core.interest.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("관심사 도메인 테스트")
class InterestTest {

    @Test
    void 관심사_생성() {
        String raw = "  Java   Spring  ";
        Interest interest = Interest.createInterest(raw);

        assertThat(interest.getId()).isNull();
        assertThat(interest.getName()).isEqualTo("java spring");
    }

    @ParameterizedTest(name = "normalize(\"{0}\") => \"{1}\"")
    @MethodSource("normalizeCases")
    void 이름_정규화(String input, String expected) {
        String actual = Interest.normalize(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 이름_정규화_시_null_입력이면_예외가_발생한다() {
        assertThatThrownBy(() -> Interest.normalize(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("관심사 이름은 null일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n", "  \t\n  "})
    void 이름_정규화_시_빈_문자열이면_예외가_발생한다(String input) {
        assertThatThrownBy(() -> Interest.normalize(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관심사 이름은 빈 문자열일 수 없습니다.");
    }

    private static Stream<Arguments> normalizeCases() {
        return Stream.of(
            Arguments.of("Java", "java"),
            Arguments.of("  Java  ", "java"),
            Arguments.of("  JAVA  SPRING  ", "java spring"),
            Arguments.of("Go\tLang", "go lang"),
            Arguments.of("  KOTLIN\nCOROUTINES  ", "kotlin coroutines"),
            Arguments.of(" sql   server ", "sql server"),
            Arguments.of("Python", "python"),
            Arguments.of("REACT", "react"),
            Arguments.of("Vue.js", "vue.js")
        );
    }
}
