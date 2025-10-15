package com.bob.domain.trade.entity.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("거래 상태 Enum 테스트")
class StatusTest {

  static Stream<Arguments> isProcessedSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, false),
        Arguments.of(Status.RESERVED, true),
        Arguments.of(Status.COMPLETED, true),
        Arguments.of(Status.CANCELED, false)
    );
  }

  static Stream<Arguments> isAbortedSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, false),
        Arguments.of(Status.REJECTED, true),
        Arguments.of(Status.CANCELED, true)
    );
  }

  static Stream<Arguments> toPostStatusValueSource() {
    return Stream.of(
        Arguments.of(Status.REQUESTED, "READY"),
        Arguments.of(Status.RESERVED, "IN_PROGRESS"),
        Arguments.of(Status.COMPLETED, "COMPLETED"),
        Arguments.of(Status.CANCELED, "READY")
    );
  }

  @ParameterizedTest(name = "{0} 상태, isProcessed 결과: {1}")
  @MethodSource("isProcessedSource")
  void 거래_진행_여부(Status status, boolean expected) {
    assertThat(status.isProcessed()).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} 상태, isAborted 결과: {1}")
  @MethodSource("isAbortedSource")
  void 거래_중단_여부(Status status, boolean expected) {
    assertThat(status.isAborted()).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{0} → {1}")
  @MethodSource("toPostStatusValueSource")
  @DisplayName("거래 상태 -> 게시글 상태 값 변환 테스트")
  void toPostStatusValue_테스트(Status status, String expected) {
    assertThat(status.toPostStatusValue()).isEqualTo(expected);
  }

  @Test
  void 쿼리_용_상태_변환() {
    // ALL 포함 -> null
    assertThat(Status.convertFrom(List.of("requested", "ALL"))).isNull();

    // 중복
    List<Status> result = Status.convertFrom(List.of("requested", "RESERVED", "requested"));
    assertThat(result).containsExactly(Status.REQUESTED, Status.RESERVED);
  }
}
