package com.bob.domain.post.repository;

import com.bob.domain.post.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

  @Query("""
      SELECT c.id FROM Category c
      WHERE c.parent.id = :parentId
      """)
  List<Integer> findChildCategoryIds(Integer parentId);
}
