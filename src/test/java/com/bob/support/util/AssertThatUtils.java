package com.bob.support.util;

import java.util.function.Consumer;

import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;

import org.springframework.test.json.JsonPathValueAssert;

public class AssertThatUtils {

    public static Consumer<AssertProvider<JsonPathValueAssert>> notNull() {
        return value -> Assertions.assertThat(value).isNotNull();
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(String expected) {
        return value -> Assertions.assertThat(value).isEqualTo(expected);
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(int expected) {
        return value -> Assertions.assertThat(value).isEqualTo(expected);
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(Long expected) {
        return value -> Assertions.assertThat(value).isEqualTo(expected);
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(boolean expected) {
        return value -> Assertions.assertThat(value).isEqualTo(expected);
    }
}
