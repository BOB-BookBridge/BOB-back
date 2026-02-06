package com.bob.admin.filter.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.filter.domain.ManagementFilterWord;
import com.bob.infrastructure.data.filter.model.FilterWord;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.filter.word.FilterWordFixture;

@DisplayName("금칙어 조회 테스트")
@ContainerTest
record ManagementFilterWordReaderTest(
    ManagementFilterWordReader managementFilterWordReader,
    FilterWordRepository wordRepository,
    EntityManager em
) {

    @Test
    void 금칙어_목록_조회() {
        wordRepository.save(FilterWordFixture.create("word1"));
        wordRepository.save(FilterWordFixture.create("word2"));

        List<ManagementFilterWord> words = managementFilterWordReader.readAll();

        assertThat(words).hasSizeGreaterThan(0);
        assertThat(words).extracting(ManagementFilterWord::getWord)
            .containsAnyOf("word1", "word2");
    }

    @Test
    void 금칙어_조회() {
        FilterWord filterWord = wordRepository.save(FilterWordFixture.create("word"));

        em.flush();
        em.clear();

        ManagementFilterWord result = managementFilterWordReader.read(filterWord.getId());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getWord()).isEqualTo("word");
        assertThat(result.isPredefined()).isFalse();
    }

    @Test
    void 금칙어_조회_시_존재하지_않으면_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> managementFilterWordReader.read(nonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("금칙어를 찾을 수 없습니다.");
    }
}
