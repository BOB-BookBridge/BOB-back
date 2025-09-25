package com.bob.domain.member.service;

import static com.bob.support.fixture.command.CreateBookCommandFixture.NEW_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID;
import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.NEW_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.entity.BookStatus;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.service.reader.MemberBookReader;
import java.util.List;
import java.util.UUID;
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
  private MemberBookReader memberBookReader;

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
    assertThat(saved.getBookStatus()).isEqualTo(BookStatus.BEST);
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
    assertThat(saved.getBookStatus()).isEqualTo(BookStatus.HIGH);
  }

  @Test
  void 회원_소유_도서_목록_정상_조회() {
    // given
    UUID memberId = MEMBER_ID;

    MemberBook mb1 = DEFAULT_MEMBER_BOOK;
    MemberBook mb2 = NEW_MEMBER_BOOK;

    given(memberBookReader.readMemberBooksByMemberId(memberId)).willReturn(List.of(mb1, mb2));
    given(bookPort.readBookSummaries(List.of(DEFAULT_MEMBER_BOOK.getBookId(), NEW_MEMBER_BOOK.getBookId()))).willReturn(DEFAULT_BOOK_RESPONSES);

    // when
    MemberBooksResponse response = service.readMemberBooksProcess(ReadMemberBooksQuery.of(memberId));

    // then
    then(memberBookReader).should().readMemberBooksByMemberId(memberId);
    then(bookPort).should().readBookSummaries(List.of(DEFAULT_MEMBER_BOOK.getBookId(), NEW_MEMBER_BOOK.getBookId()));

    List<MemberBookSummary> summaries = response.books();
    assertThat(summaries).hasSize(2);

    MemberBookSummary s1 = summaries.get(0);
    assertThat(s1.id()).isEqualTo(DEFAULT_MEMBER_BOOK.getId());
    assertThat(s1.bookId()).isEqualTo(DEFAULT_MEMBER_BOOK.getBookId());
    assertThat(s1.bookStatus()).isEqualTo(BookStatus.BEST.name());

    MemberBookSummary s2 = summaries.get(1);
    assertThat(s2.id()).isEqualTo(NEW_MEMBER_BOOK.getId());
    assertThat(s2.bookId()).isEqualTo(NEW_MEMBER_BOOK.getBookId());
    assertThat(s2.bookStatus()).isEqualTo(BookStatus.HIGH.name());
  }
}
