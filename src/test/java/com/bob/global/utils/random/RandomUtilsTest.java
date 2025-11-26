package com.bob.global.utils.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@DisplayName("랜덤 유틸 테스트")
class RandomUtilsTest {

    @Test
    void 랜덤_코드_생성() {
        int size = 12;

        String code = RandomUtils.generateCode(size);

        assertThat(code).hasSize(size);
    }

    @Test
    void 랜덤_코드_검증() {
        String code = RandomUtils.generateCode(20);

        assertThat(code).matches("[A-Za-z0-9]+");
    }

    @RepeatedTest(3)
    void 랜덤_코드_미중복_검증() {
        int size = 16;
        Set<String> generated = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            generated.add(RandomUtils.generateCode(size));
        }

        assertThat(generated).hasSizeGreaterThan(45);
    }
}
