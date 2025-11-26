package com.bob.core.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.category.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query("""
        SELECT c.id FROM Category c
        WHERE c.parent.id = :parentId
        """)
    List<Integer> findChildCategoryIds(Integer parentId);
}
