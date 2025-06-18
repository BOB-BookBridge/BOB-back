package com.bob.domain.member.service.port;

import java.util.Random;
import java.util.stream.Collectors;

public interface MemberMailPort {

  void sendCodeProcess(String email);

  void verifyCodeProcess(String email, String code);

  String sendTempPasswordProcess(String email);

  default String generateCode(int size) {
    String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    Random random = new Random();
    return random.ints(size, 0, candidateChars.length())
        .mapToObj(candidateChars::charAt)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }
}
