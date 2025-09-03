package com.bob.support.fixture.domain;

import com.bob.domain.member.entity.Interest;

public class InterestFixture {

  public static Interest customInterest(Long id, String canonicalName) {
    return Interest.builder()
        .id(id)
        .canonicalName(canonicalName)
        .build();
  }
}
