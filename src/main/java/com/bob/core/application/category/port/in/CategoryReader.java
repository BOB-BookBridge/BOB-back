package com.bob.core.application.category.port.in;

import java.util.List;

import com.bob.core.domain.category.Category;

public interface CategoryReader {

    Category read(Integer categoryId);

    List<Integer> readChildCategoryIds(Integer parentCategoryId);
}
