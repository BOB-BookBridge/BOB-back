package com.bob.admin.filter.application.port.in;

import static com.bob.global.exception.response.ApplicationError.FILTER_WORD_NOT_EDITABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.infrastructure.data.filter.model.FilterWord;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.filter.word.FilterWordFixture;

@DisplayName("금칙어 삭제 테스트")
@ContainerTest
record ManagementFilterWordRemoverTest(
    ManagementFilterWordRemover filterWordRemover,
    FilterWordRepository wordRepository
) {

    @Test
    void 금칙어_삭제() {
        String keyword = "삭제테스트";
        FilterWord word = wordRepository.save(FilterWordFixture.create(keyword));
        assertThat(wordRepository.existsByKeyword(keyword)).isTrue();

        filterWordRemover.remove(word.getId());

        assertThat(wordRepository.existsByKeyword(keyword)).isFalse();
    }

    @Test
    void 사전_정의_된_금칙어_삭제_시_예외가_발생한다() {
        Long predefinedWordId = wordRepository.findAll().get(0).getId();

        assertThatThrownBy(() -> filterWordRemover.remove(predefinedWordId))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(FILTER_WORD_NOT_EDITABLE.getMessage());
    }
}
