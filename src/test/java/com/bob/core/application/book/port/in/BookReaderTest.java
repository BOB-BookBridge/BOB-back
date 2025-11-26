package com.bob.core.application.book.port.in;

import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;
import static com.bob.support.fixture.book.domain.BookFixture.createBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.book.dto.query.ReadBooksQuery;
import com.bob.core.domain.book.Book;
import com.bob.core.domain.book.repository.BookRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책 조회 테스트")
@ContainerTest
record BookReaderTest(BookReader bookReader, BookRepository bookRepository, EntityManager em) {

    @Test
    void 책_목록_조회() {
        Book book1 = bookRepository.save(createBook(DEFAULT_ISBN));
        Book book2 = bookRepository.save(createBook("0000000000000"));
        List<Long> ids = List.of(book1.getId(), book2.getId());

        em.flush();
        em.clear();

        List<Book> result = bookReader.readAll(ids);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(result.get(1).getIsbn()).isEqualTo("0000000000000");
    }

    @Test
    void 책_조회() {
        Book book = createBook(DEFAULT_ISBN);
        bookRepository.save(book);
        Long id = book.getId();

        em.flush();
        em.clear();

        Book result = bookReader.read(id);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(DEFAULT_ISBN);
    }

    @Test
    void 책_조회_시_존재하지_않는_책인_경우_예외가_발생한다() {
        Long id = 999L;

        assertThatThrownBy(() -> bookReader.read(id))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.NOT_EXIST_OBJECT.getMessage());
    }

    @Test
    void ISBN_기반_책_조회() {
        Book book = createBook(DEFAULT_ISBN);
        bookRepository.save(book);

        em.flush();
        em.clear();

        Optional<Book> result = bookReader.read(DEFAULT_ISBN);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getIsbn()).isEqualTo(DEFAULT_ISBN);
    }

    @Test
    void ISBN_기반_책_조회_시_존재하지_않는_책인_경우_Empty를_반환한다() {
        String nonExistIsbn = "0000000000000";

        Optional<Book> result = bookReader.read(nonExistIsbn);

        assertThat(result).isEmpty();
    }

    @Test
    void 키워드_기반_책_목록_조회() {
        Book spring1 = bookRepository.save(createBook("1111111111111", "spring boot"));
        Book spring2 = bookRepository.save(createBook("2222222222222", "spring data jpa"));
        Book java = bookRepository.save(createBook("3333333333333", "java programming"));

        String key = "TITLE";
        String keyword = "spring";

        ReadBooksQuery query = ReadBooksQuery.of(key, keyword);
        List<Long> result = bookReader.readAllIdsByQuery(query);

        assertThat(result).containsExactly(spring1.getId(), spring2.getId());
        assertThat(result).doesNotContain(java.getId());
    }
}
