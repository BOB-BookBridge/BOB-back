package com.bob.core.application.category.port.in;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.domain.category.Category;
import com.bob.core.domain.category.repository.CategoryRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("카테고리 조회 테스트")
@ContainerTest
record CategoryReaderTest(CategoryReader categoryReader, CategoryRepository categoryRepository) {

    @Test
    void 카테고리_조회() {
        Category category = categoryReader.read(21);

        assertThat(category).isNotNull();
        assertThat(category.getId()).isEqualTo(21);
        assertThat(category.getName()).isEqualTo("프로그래밍");
        assertThat(category.getParent().getId()).isEqualTo(2);
    }

    @Test
    void 카테고리_조회_시_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> categoryReader.read(-1))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(UN_SUPPORTED_CATEGORY.getMessage());
    }

    @Test
    void 자식_카테고리_ID_조회() {
        int parentId = 2;

        List<Integer> children = categoryReader.readChildCategoryIds(parentId);

        assertThat(children).isNotEmpty();
        assertThat(children).containsExactly(17, 18, 19, 20, 21);
    }
}
