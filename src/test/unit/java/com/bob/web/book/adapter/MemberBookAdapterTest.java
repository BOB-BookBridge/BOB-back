package com.bob.web.book.adapter;

import static com.bob.support.fixture.command.CreateBookCommandFixture.DEFAULT_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSE;
import static com.bob.support.fixture.response.BookResponseFixture.SECOND_BOOK_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBooksQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("MemberBookAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class MemberBookAdapterTest {

  @InjectMocks
  private MemberBookAdapter adapter;

  @Mock
  private BookWriteUseCase writeUseCase;

  @Mock
  private BookReadUseCase readUseCase;

  @Test
  void 도서_생성_기능_호출() {
    // given
    CreateBookCommand command = DEFAULT_CREATE_BOOK_COMMAND;
    given(writeUseCase.createBookProcess(command)).willReturn(1L);

    // when
    Long result = adapter.createBook(command);

    // then
    assertThat(result).isEqualTo(1L);
    then(writeUseCase).should().createBookProcess(command);
  }

  @Test
  void 도서_ID_목록_조회_기능_호출() {
    // given
    List<Long> ids = List.of(1L, 2L);
    List<BookResponse> expected = List.of(DEFAULT_BOOK_RESPONSE, SECOND_BOOK_RESPONSE);
    given(readUseCase.readBooksProcess(ReadBooksQuery.of(ids))).willReturn(expected);

    // when
    List<BookResponse> result = adapter.readBookSummaries(ids);

    // then
    assertThat(result).isSameAs(expected);
    then(readUseCase).should().readBooksProcess(ReadBooksQuery.of(ids));
  }
}
