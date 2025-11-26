package com.bob.global.utils.stream;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamUtils {

    private StreamUtils() {
    }

    public static <T> void forEachWithIndex(List<T> list, BiConsumer<Integer, T> indexedConsumer) {
        IntStream.range(0, list.size())
            .forEach(i -> indexedConsumer.accept(i, list.get(i)));
    }

    /**
     * @param input 정렬할 입력 리스트
     * @param key   정렬 기준이 되는 값을 추출하는 함수
     * @param <T>   리스트 요소 타입
     * @param <U>   정렬 기준 키의 타입 (Comparable)
     * @return null이 아닌 요소들로 이루어진, 내림차순 정렬된 새 리스트
     */
    public static <T, U extends Comparable<? super U>> List<T> sortByDesc(List<T> input, Function<T, U> key) {
        return input.stream()
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(key, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }
}
