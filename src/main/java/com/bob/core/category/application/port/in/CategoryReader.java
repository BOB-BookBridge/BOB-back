package com.bob.core.category.application.port.in;

import java.util.List;

import com.bob.core.category.domain.Category;

public interface CategoryReader {

    Category read(Integer categoryId);

    List<Integer> readChildCategoryIds(Integer parentCategoryId);
}
