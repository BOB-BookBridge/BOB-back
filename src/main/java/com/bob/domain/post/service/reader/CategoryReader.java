package com.bob.domain.post.service.reader;

import com.bob.domain.post.entity.Category;
import com.bob.domain.post.repository.CategoryRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryReader {

  private final CategoryRepository categoryRepository;

  public Category readCategoryById(Integer categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ApplicationException(ApplicationError.UN_SUPPORTED_CATEGORY));
  }

  public List<Integer> readChildCategoryIds(Integer parentCategoryId) {
    return categoryRepository.findChildCategoryIds(parentCategoryId);
  }
}
