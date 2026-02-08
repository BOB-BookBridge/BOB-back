package com.bob.admin.filter.domain;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagementFilterWord {

    private Long id;
    private String word;
    private boolean predefined;
    private LocalDateTime createdAt;

    public static ManagementFilterWord create(String word) {
        return ManagementFilterWord.builder()
            .word(word)
            .predefined(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public boolean isEditable() {
        return !predefined;
    }
}
