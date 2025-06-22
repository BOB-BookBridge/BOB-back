package com.bob.global.utils.funtional;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class FunctionalUtils {

  public static <T, R> List<R> mapWithSequence(List<T> list, BiFunction<T, Integer, R> mapper) {
    return IntStream.range(0, list.size())
        .mapToObj(seq -> mapper.apply(list.get(seq), seq))
        .toList();
  }
}
