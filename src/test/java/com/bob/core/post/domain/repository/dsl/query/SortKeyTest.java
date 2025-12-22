package com.bob.core.post.domain.repository.dsl.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("정렬 키 조건 테스트")
class SortKeyTest {

    @Test
    void null_입력시_RECENT_반환() {
        Optional<SortKey> result = SortKey.from(null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.RECENT);
    }

    @Test
    void RECENT_입력시_RECENT_반환() {
        Optional<SortKey> result = SortKey.from("RECENT");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.RECENT);
    }

    @Test
    void OLD_입력시_OLD_반환() {
        Optional<SortKey> result = SortKey.from("OLD");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.OLD);
    }

    @Test
    void LOW_PRICE_입력시_LOW_PRICE_반환() {
        Optional<SortKey> result = SortKey.from("LOW_PRICE");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.LOW_PRICE);
    }

    @Test
    void HIGH_PRICE_입력시_HIGH_PRICE_반환() {
        Optional<SortKey> result = SortKey.from("HIGH_PRICE");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.HIGH_PRICE);
    }

    @Test
    void 소문자_입력시_대소문자_무관하게_반환() {
        Optional<SortKey> result = SortKey.from("recent");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.RECENT);
    }

    @Test
    void 대소문자_섞인_입력시_정상_반환() {
        Optional<SortKey> result = SortKey.from("Low_Price");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SortKey.LOW_PRICE);
    }

    @Test
    void 잘못된_입력시_empty_반환() {
        Optional<SortKey> result = SortKey.from("INVALID_KEY");

        assertThat(result).isEmpty();
    }

    @Test
    void 빈_문자열_입력시_empty_반환() {
        Optional<SortKey> result = SortKey.from("");

        assertThat(result).isEmpty();
    }
}
