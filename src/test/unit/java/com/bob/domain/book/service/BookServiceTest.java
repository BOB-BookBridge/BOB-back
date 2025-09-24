package com.bob.domain.book.service;

import static com.bob.support.fixture.command.CreateBookCommandFixture.DEFAULT_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.SearchBookQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.service.reader.BookReader;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("도서 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @InjectMocks
  private BookService bookService;

  @Mock
  private BookReader bookReader;

  @Mock
  private BookRepository bookRepository;

  @Test
  void 이미_존재하는_도서가_있으면_저장하지_않고_조회된_도서를_반환한다() {
    // given
    CreateBookCommand command = DEFAULT_CREATE_BOOK_COMMAND;
    Book existingBook = DEFAULT_BOOK;
    given(bookReader.readOptionalBookByIsbn(command.isbn13())).willReturn(Optional.of(existingBook));

    // when
    Long result = bookService.createBookProcess(command);

    // then
    assertThat(result).isEqualTo(existingBook.getId());
    verify(bookReader).readOptionalBookByIsbn(command.isbn13());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void 존재하지_않는_도서면_새로_저장하고_반환한다() {
    // given
    CreateBookCommand command = DEFAULT_CREATE_BOOK_COMMAND;
    Book newBook = command.toBook();
    given(bookReader.readOptionalBookByIsbn(command.isbn13())).willReturn(Optional.empty());
    given(bookRepository.save(any(Book.class))).willReturn(newBook);

    // when
    Long result = bookService.createBookProcess(command);

    // then
    assertThat(result).isEqualTo(newBook.getId());
    verify(bookReader).readOptionalBookByIsbn(command.isbn13());
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  @DisplayName("도서 상세 조회 - 성공")
  void 도서_상세를_조회할_수_있다() {
    // given
    Book book = DEFAULT_BOOK;
    given(bookReader.readBookById(book.getId())).willReturn(book);

    // when
    BookResponse response = bookService.readBookProcess(ReadBookDetailQuery.of(book.getId()));

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(book.getId());
    then(bookReader).should().readBookById(book.getId());
  }

  @Test
  void 키워드_기반_도서_검색_결과_반환() {
    // given
    String key = "TITLE";
    String keyword = "spring";
    List<Long> expected = List.of(1L, 2L, 3L);
    given(bookReader.searchBookIdsByKeyword(key, keyword)).willReturn(expected);

    // when
    List<Long> ids = bookService.searchBookIdsProcess(SearchBookQuery.of(key, keyword));

    // then
    assertThat(ids).containsExactlyElementsOf(expected);
    then(bookReader).should().searchBookIdsByKeyword(key, keyword);
  }

  @Test
  void 키워드_기반_도서_검색_결과_0건_빈_리스트_반환() {
    // given
    String key = "AUTHOR";
    String keyword = "nohit";
    given(bookReader.searchBookIdsByKeyword(key, keyword)).willReturn(List.of());

    // when
    List<Long> ids = bookService.searchBookIdsProcess(SearchBookQuery.of(key, keyword));

    // then
    assertThat(ids).isEmpty();
    then(bookReader).should().searchBookIdsByKeyword(key, keyword);
    then(bookRepository).shouldHaveNoInteractions();
  }
}