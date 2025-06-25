package com.bob.global.utils.stream;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class StreamUtils {

  private StreamUtils() {
  }

  public static <T> void forEachWithIndex(List<T> list, BiConsumer<Integer, T> indexedConsumer) {
    IntStream.range(0, list.size())
        .forEach(i -> indexedConsumer.accept(i, list.get(i)));
  }
}
