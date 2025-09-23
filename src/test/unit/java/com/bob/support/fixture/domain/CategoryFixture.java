package com.bob.support.fixture.domain;

import com.bob.domain.post.entity.Category;

public class CategoryFixture {

  public static final Integer DEFAULT_CATEGORY_ID = 1;
  public static final String DEFAULT_CATEGORY_NAME = "경제/경영";

  public static Category defaultCategory() {
    return Category.builder()
        .id(DEFAULT_CATEGORY_ID)
        .name(DEFAULT_CATEGORY_NAME)
        .build();
  }

  public static Category defaultCategory2() {
    return Category.builder()
        .name(DEFAULT_CATEGORY_NAME)
        .build();
  }
}
