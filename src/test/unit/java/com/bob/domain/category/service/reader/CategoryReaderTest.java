package com.bob.domain.category.service.reader;

import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.domain.post.entity.Category;
import com.bob.domain.post.repository.CategoryRepository;
import com.bob.domain.post.service.reader.CategoryReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("CategoryReader 테스트")
@ExtendWith(MockitoExtension.class)
class CategoryReaderTest {

  @InjectMocks
  private CategoryReader categoryReader;

  @Mock
  private CategoryRepository categoryRepository;

  @Test
  @DisplayName("카테고리 ID로 조회 - 존재하는 경우")
  void 카테고리ID로_조회하면_존재하는_경우_정상_반환한다() {
    // given
    Category category = defaultCategory();
    given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

    // when
    Category result = categoryReader.readCategoryById(category.getId());

    // then
    assertThat(result).isEqualTo(category);
    verify(categoryRepository).findById(category.getId());
  }

  @Test
  @DisplayName("카테고리 ID로 조회 - 존재하지 않는 경우 예외 발생")
  void 카테고리ID로_조회_시_존재하지_않으면_예외를_던진다() {
    // given
    Integer categoryId = -1;
    given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> categoryReader.readCategoryById(categoryId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UN_SUPPORTED_CATEGORY.getMessage());

    verify(categoryRepository).findById(categoryId);
  }

  @Test
  @DisplayName("자식 카테고리 ID 조회 - 자식이 존재하는 경우")
  void 부모_카테고리ID로_자식_카테고리ID_리스트를_조회할_수_있다() {
    // given
    Integer parentId = 1;
    given(categoryRepository.findChildCategoryIds(parentId)).willReturn(List.of(12, 13, 14, 15, 16));

    // when
    List<Integer> result = categoryReader.readChildCategoryIds(parentId);

    // then
    assertThat(result).containsExactly(12, 13, 14, 15, 16);
    verify(categoryRepository).findChildCategoryIds(parentId);
  }

  @Test
  @DisplayName("자식 카테고리 ID 조회 - 자식이 없는 경우 빈 리스트 반환")
  void 최하위_카테고리인_경우_빈_리스트를_반환한다() {
    // given
    Integer parentId = 60;
    given(categoryRepository.findChildCategoryIds(parentId)).willReturn(List.of());

    // when
    List<Integer> result = categoryReader.readChildCategoryIds(parentId);

    // then
    assertThat(result).isEmpty();
    verify(categoryRepository).findChildCategoryIds(parentId);
  }
}
