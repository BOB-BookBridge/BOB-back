package com.bob.global.utils.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Stream 유틸 테스트")
class StreamUtilsTest {

    @Test
    void 인덱스_요소_매핑() {
        List<String> input = List.of("A", "B", "C");

        List<Integer> capturedIndices = new ArrayList<>();
        List<String> capturedValues = new ArrayList<>();

        StreamUtils.forEachWithIndex(input, (i, val) -> {
            capturedIndices.add(i);
            capturedValues.add(val);
        });

        assertThat(capturedIndices).containsExactly(0, 1, 2);
        assertThat(capturedValues).containsExactly("A", "B", "C");
    }

    @Test
    void 빈_리스트_매핑() {
        List<String> emptyList = List.of();
        List<Integer> capturedIndices = new ArrayList<>();

        StreamUtils.forEachWithIndex(emptyList, (i, val) -> capturedIndices.add(i));

        assertThat(capturedIndices).isEmpty();
    }

    @Test
    void 키_기반_내림차순_정렬() {
        List<Dummy> input = new ArrayList<>(List.of(
            Dummy.of("A", 5),
            Dummy.of("B", 10),
            Dummy.of("C", 7)
        ));
        input.add(1, null);

        List<Dummy> result = StreamUtils.sortByDesc(input, Dummy::score);

        assertThat(result).extracting(Dummy::name).containsExactly("B", "C", "A");
    }

    private record Dummy(String name, int score) {

        static Dummy of(String name, int score) {
            return new Dummy(name, score);
        }
    }
}
