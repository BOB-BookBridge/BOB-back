package com.bob.web.book.adapter;

import static com.bob.support.fixture.command.CreateBookCommandFixture.DEFAULT_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.SearchBookQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PostBookAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class PostBookAdapterTest {

  @InjectMocks
  private PostBookAdapter adapter;

  @Mock
  private BookWriteUseCase writeUseCase;

  @Mock
  private BookReadUseCase readUseCase;

  @Test
  void 책_생성_기능_호출() {
    // given
    CreateBookCommand command = DEFAULT_CREATE_BOOK_COMMAND;
    given(writeUseCase.createBookProcess(command)).willReturn(DEFAULT_BOOK_RESPONSE.id());

    // when
    Long id = adapter.createBook(command);

    // then
    assertThat(id).isEqualTo(DEFAULT_BOOK_RESPONSE.id());
    then(writeUseCase).should(times(1)).createBookProcess(command);
  }

  @Test
  void 책_정보_조회_기능_호출() {
    // given
    ReadBookDetailQuery query = new ReadBookDetailQuery(DEFAULT_BOOK_RESPONSE.id());
    given(readUseCase.readBookProcess(any(ReadBookDetailQuery.class))).willReturn(DEFAULT_BOOK_RESPONSE);

    // when
    BookResponse response = adapter.readBookSummary(DEFAULT_BOOK_RESPONSE.id());

    // then
    assertThat(response.id()).isEqualTo(DEFAULT_BOOK_RESPONSE.id());
    assertThat(response.isbn13()).isSameAs(DEFAULT_BOOK_RESPONSE.isbn13());
    assertThat(response.author()).isSameAs(DEFAULT_BOOK_RESPONSE.author());
    assertThat(response.title()).isEqualTo(DEFAULT_BOOK_RESPONSE.title());
    then(readUseCase).should(times(1)).readBookProcess(query);
  }

  @Test
  void 키워드_기반_책_ID_조회_기능_호출() {
    // given
    String key = "ALL";
    String keyword = "spring";
    SearchBookQuery query = new SearchBookQuery(key, keyword);
    given(readUseCase.searchBookIdsProcess(query)).willReturn(List.of(1L, 2L, 3L));

    // when
    List<Long> ids = adapter.searchBookIds(key, keyword);

    // then
    assertThat(ids).containsExactly(1L, 2L, 3L);
    ArgumentCaptor<SearchBookQuery> captor = ArgumentCaptor.forClass(SearchBookQuery.class);
    then(readUseCase).should(times(1)).searchBookIdsProcess(query);
  }
}