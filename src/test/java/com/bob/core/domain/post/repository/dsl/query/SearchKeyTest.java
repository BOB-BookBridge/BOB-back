package com.bob.core.domain.post.repository.dsl.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("검색 키 조건 테스트")
class SearchKeyTest {

    @Test
    void null_입력시_ALL_반환() {
        Optional<SearchKey> result = SearchKey.from(null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchKey.ALL);
    }

    @Test
    void 통합_입력시_ALL_반환() {
        Optional<SearchKey> result = SearchKey.from("통합");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchKey.ALL);
    }

    @Test
    void 제목_입력시_TITLE_반환() {
        Optional<SearchKey> result = SearchKey.from("제목");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchKey.TITLE);
    }

    @Test
    void 저자_입력시_AUTHOR_반환() {
        Optional<SearchKey> result = SearchKey.from("저자");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(SearchKey.AUTHOR);
    }

    @Test
    void 잘못된_입력시_empty_반환() {
        Optional<SearchKey> result = SearchKey.from("잘못된값");

        assertThat(result).isEmpty();
    }
}
