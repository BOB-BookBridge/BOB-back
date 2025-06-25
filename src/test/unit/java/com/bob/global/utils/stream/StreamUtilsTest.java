package com.bob.global.utils.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StreamUtilsTest {

  @DisplayName("인덱스에 따른 데이터 매핑 테스트")
  @Test
  void 인덱스와_요소가_정상적으로_순회된다() {
    // given
    List<String> input = List.of("A", "B", "C");

    List<Integer> capturedIndices = new ArrayList<>();
    List<String> capturedValues = new ArrayList<>();

    // when
    StreamUtils.forEachWithIndex(input, (i, val) -> {
      capturedIndices.add(i);
      capturedValues.add(val);
    });

    // then
    assertThat(capturedIndices).containsExactly(0, 1, 2);
    assertThat(capturedValues).containsExactly("A", "B", "C");
  }

  @DisplayName("빈 리스트 매핑 테스트")
  @Test
  void 빈_리스트는_아무_작업도_수행하지_않는다() {
    // given
    List<String> emptyList = List.of();
    List<Integer> capturedIndices = new ArrayList<>();

    // when
    StreamUtils.forEachWithIndex(emptyList, (i, val) -> capturedIndices.add(i));

    // then
    assertThat(capturedIndices).isEmpty();
  }
}
