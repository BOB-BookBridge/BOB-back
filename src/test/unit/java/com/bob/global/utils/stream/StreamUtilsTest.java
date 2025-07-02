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

  @DisplayName("null 요소를 제외, 정렬 키 기준 내림차순 정렬 테스트")
  @Test
  void null을_제외하고_키_기준으로_내림차순_정렬된다() {
    // given
    List<Dummy> input = new ArrayList<>(List.of(
        Dummy.of("A", 5),
        Dummy.of("B", 10),
        Dummy.of("C", 7)
    ));
    input.add(1, null);
    
    // when
    List<Dummy> result = StreamUtils.sortByDesc(input, Dummy::score);

    // then
    assertThat(result)
        .extracting(Dummy::name)
        .containsExactly("B", "C", "A");
  }

  private record Dummy(String name, int score) {

    static Dummy of(String name, int score) {
      return new Dummy(name, score);
    }
  }
}
