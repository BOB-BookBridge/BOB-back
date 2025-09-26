package com.bob.domain.trade.entity.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("거래 상태 Enum 테스트")
class StatusTest {

  @ParameterizedTest(name = "{0} 상태의 isProcessed 결과는 {1}")
  @MethodSource("isProcessedSource")
  @DisplayName("거래 진행 여부 반환 테스트 ")
  void isProcessed_테스트(Status status, boolean expected) {
    assertThat(status.isProcessed()).isEqualTo(expected);
  }

  static Stream<Arguments> isProcessedSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, false),
        Arguments.of(Status.RESERVED, true),
        Arguments.of(Status.COMPLETED, true),
        Arguments.of(Status.CANCELED, false)
    );
  }

  @ParameterizedTest(name = "{0} → {1}")
  @MethodSource("toPostStatusValueSource")
  @DisplayName("거래 상태 -> 게시글 상태 값 변환 테스트")
  void toPostStatusValue_테스트(Status status, String expected) {
    assertThat(status.toPostStatusValue()).isEqualTo(expected);
  }

  static Stream<Arguments> toPostStatusValueSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, "READY"),
        Arguments.of(Status.RESERVED, "IN_PROGRESS"),
        Arguments.of(Status.COMPLETED, "COMPLETED"),
        Arguments.of(Status.CANCELED, "READY")
    );
  }

  @ParameterizedTest(name = "{0} → {1}")
  @MethodSource("valueMethodSource")
  @DisplayName("value() 메서드 테스트")
  void value_메서드_테스트(Status status, String expected) {
    assertThat(status.value()).isEqualTo(expected);
  }

  static Stream<Arguments> valueMethodSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, "대기"),
        Arguments.of(Status.RESERVED, "예약"),
        Arguments.of(Status.COMPLETED, "완료"),
        Arguments.of(Status.CANCELED, "취소")
    );
  }
}
