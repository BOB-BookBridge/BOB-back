package com.bob.support.fixture.filter.word;

import java.time.LocalDateTime;

import com.bob.infrastructure.data.filter.model.FilterWord;

public class FilterWordFixture {

    public static FilterWord create(String word) {
        return FilterWord.builder()
            .word(word)
            .predefined(false)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
