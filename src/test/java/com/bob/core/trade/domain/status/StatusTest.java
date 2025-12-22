package com.bob.core.trade.domain.status;

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

    @ParameterizedTest(name = "{0} → {1}")
    @MethodSource("toPostStatusValueSource")
    void 게시글_상태_값_변환(Status status, String expected) {
        assertThat(status.toPostStatusValue()).isEqualTo(expected);
    }

    private static Stream<Arguments> toPostStatusValueSource() {
        return Stream.of(
            Arguments.of(Status.REQUESTED, "READY"),
            Arguments.of(Status.RESERVED, "RESERVED"),
            Arguments.of(Status.COMPLETED, "COMPLETED"),
            Arguments.of(Status.CANCELED, "READY")
        );
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
