package com.bob.core.book.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.book.domain.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findAllByIdIn(List<Long> ids);

    Optional<Book> findByIsbn(String isbn);

    @Query("""
        SELECT DISTINCT b.id
        FROM Book b
        WHERE ((:key = 'TITLE'  AND LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%')))
            OR (:key = 'AUTHOR' AND LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
            OR (:key = 'ALL'    AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR LOWER(b.author)  LIKE LOWER(CONCAT('%', :keyword, '%'))))
        )
        """)
    List<Long> findIdsByKeyword(String key, String keyword);
}
