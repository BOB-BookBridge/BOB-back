package com.bob.global.utils.random;

import java.util.Random;
import java.util.stream.Collectors;

public class RandomUtils {

  public static String generateCode(int size) {
    String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    return new Random().ints(size, 0, candidateChars.length())
        .mapToObj(candidateChars::charAt)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }
}
