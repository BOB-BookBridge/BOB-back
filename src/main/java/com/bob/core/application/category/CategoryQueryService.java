package com.bob.core.application.category;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.category.port.in.CategoryReader;
import com.bob.core.domain.category.Category;
import com.bob.core.domain.category.repository.CategoryRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryQueryService implements CategoryReader {

    private final CategoryRepository categoryRepository;

    @Override
    public Category read(Integer categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ApplicationException(ApplicationError.UN_SUPPORTED_CATEGORY));
    }

    @Override
    public List<Integer> readChildCategoryIds(Integer parentCategoryId) {
        return categoryRepository.findChildCategoryIds(parentCategoryId);
    }
}
