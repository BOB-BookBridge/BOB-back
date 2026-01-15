package com.bob.core.member.domain.repository.dsl.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchKey {
    ALL, EMAIL, NICKNAME;

    public static SearchKey from(String value) {
        if (value == null || value.isEmpty())
            return ALL;

        return valueOf(value.toUpperCase());
    }
}
