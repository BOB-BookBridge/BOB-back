package com.bob.domain.book.repository;

import com.bob.domain.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends CrudRepository<Book, Long> {

  Optional<Book> findByIsbn13(String isbn);

  @Query("""
      SELECT DISTINCT b.id
      FROM Book b
      WHERE ((:key = 'TITLE'  AND LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%')))
          OR (:key = 'AUTHOR' AND LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
          OR (:key = 'ALL'    AND (LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))))
      )
      """)
  List<Long> findIdsByKeyword(@Param("key") String key, @Param("keyword") String keyword);
}
