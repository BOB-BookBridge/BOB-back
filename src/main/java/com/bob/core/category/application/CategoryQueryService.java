package com.bob.core.category.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.category.application.port.in.CategoryReader;
import com.bob.core.category.domain.Category;
import com.bob.core.category.domain.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryQueryService implements CategoryReader {

    private final CategoryRepository categoryRepository;

    @Override
    public Category read(Integer categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. id : " + categoryId));
    }

    @Override
    public List<Integer> readChildCategoryIds(Integer parentCategoryId) {
        return categoryRepository.findChildCategoryIds(parentCategoryId);
    }
}
