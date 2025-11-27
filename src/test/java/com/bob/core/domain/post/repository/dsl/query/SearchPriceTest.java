package com.bob.core.domain.post.repository.dsl.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("가격 검색 조건 테스트")
class SearchPriceTest {

    @Test
    void null_입력시_empty_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(null);

        assertThat(result).isEmpty();
    }

    @Test
    void 음수_입력시_empty_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(-1);

        assertThat(result).isEmpty();
    }

    @Test
    void 범위_초과_입력시_empty_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(4);

        assertThat(result).isEmpty();
    }

    @Test
    void 인덱스_0_입력시_UNDER_5000_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(0);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchPrice.UNDER_5000);
    }

    @Test
    void 인덱스_1_입력시_BETWEEN_5000_AND_10000_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(1);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchPrice.BETWEEN_5000_AND_10000);
    }

    @Test
    void 인덱스_2_입력시_BETWEEN_10000_AND_20000_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(2);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchPrice.BETWEEN_10000_AND_20000);
    }

    @Test
    void 인덱스_3_입력시_OVER_20000_반환() {
        Optional<SearchPrice> result = SearchPrice.fromIndex(3);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchPrice.OVER_20000);
    }

    @Test
    void UNDER_5000_가격_범위_확인() {
        SearchPrice price = SearchPrice.UNDER_5000;

        assertThat(price.getMinPrice()).isEqualTo(0);
        assertThat(price.getMaxPrice()).isEqualTo(5000);
    }

    @Test
    void BETWEEN_5000_AND_10000_가격_범위_확인() {
        SearchPrice price = SearchPrice.BETWEEN_5000_AND_10000;

        assertThat(price.getMinPrice()).isEqualTo(5000);
        assertThat(price.getMaxPrice()).isEqualTo(10000);
    }

    @Test
    void BETWEEN_10000_AND_20000_가격_범위_확인() {
        SearchPrice price = SearchPrice.BETWEEN_10000_AND_20000;

        assertThat(price.getMinPrice()).isEqualTo(10000);
        assertThat(price.getMaxPrice()).isEqualTo(20000);
    }

    @Test
    void OVER_20000_가격_범위_확인() {
        SearchPrice price = SearchPrice.OVER_20000;

        assertThat(price.getMinPrice()).isEqualTo(20000);
        assertThat(price.getMaxPrice()).isEqualTo(Integer.MAX_VALUE);
    }
}
