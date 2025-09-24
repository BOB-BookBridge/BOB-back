package com.bob.web.book.adapter;

import static com.bob.support.fixture.command.CreateBookCommandFixture.DEFAULT_CREATE_BOOK_COMMAND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.usecase.BookWriteUseCase;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberBookAdapterTest {

  @InjectMocks
  private MemberBookAdapter adapter;

  @Mock
  private BookWriteUseCase writeUseCase;

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
}
