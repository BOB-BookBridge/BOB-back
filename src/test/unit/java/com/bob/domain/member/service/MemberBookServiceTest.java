package com.bob.domain.member.service;

import static com.bob.support.fixture.command.CreateBookCommandFixture.NEW_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID;
import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.NEW_MEMBER_BOOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.port.out.MemberBookPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 도서 서비스 테스트")
class MemberBookServiceTest {

  @InjectMocks
  private MemberBookService service;

  @Mock
  private MemberBookRepository memberBookRepository;

  @Mock
  private MemberBookPort bookPort;

  @Captor
  private ArgumentCaptor<MemberBook> memberBookCaptor;

  @Test
  void 회원_도서_정상_등록_이미_등록된_도서() {
    // given
    RegisterMemberBookCommand command = DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
    given(memberBookRepository.save(any(MemberBook.class))).willReturn(DEFAULT_MEMBER_BOOK);

    // when
    service.registerMemberBookProcess(command);

    // then
    then(memberBookRepository).should().save(memberBookCaptor.capture());

    MemberBook saved = memberBookCaptor.getValue();
    assertThat(saved.getMemberId()).isEqualTo(DEFAULT_REGISTER_MEMBER_BOOK_COMMAND.memberId());
    assertThat(saved.getBookId()).isEqualTo(DEFAULT_REGISTER_MEMBER_BOOK_COMMAND.bookId());
    assertThat(saved.getStatus().name()).isEqualTo(DEFAULT_REGISTER_MEMBER_BOOK_COMMAND.status());
  }

  @Test
  void 회원_도서_정상_등록_도서_id가_null이면_도서_생성_및_회원_도서_등록() {
    // given
    RegisterMemberBookCommand command = REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID;
    given(bookPort.createBook(NEW_CREATE_BOOK_COMMAND)).willReturn(2L);
    given(memberBookRepository.save(any(MemberBook.class))).willReturn(NEW_MEMBER_BOOK);

    // when
    service.registerMemberBookProcess(command);

    // then
    then(bookPort).should(times(1)).createBook(NEW_CREATE_BOOK_COMMAND);
    then(memberBookRepository).should(times(1)).save(memberBookCaptor.capture());

    MemberBook saved = memberBookCaptor.getValue();
    assertThat(saved.getMemberId()).isEqualTo(REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID.memberId());
    assertThat(saved.getBookId()).isEqualTo(NEW_MEMBER_BOOK.getBookId());
    assertThat(saved.getStatus().name()).isEqualTo(REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID.status());
  }
}
