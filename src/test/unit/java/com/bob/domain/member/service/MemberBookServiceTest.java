package com.bob.domain.member.service;

import static com.bob.support.fixture.command.CreateBookCommandFixture.NEW_CREATE_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID;
import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.DIFF_IN_TRADE_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.NEW_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.SAME_IN_TRADE_BOOK;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSE;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.BookStatus;
import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.service.reader.MemberBookReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
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
@DisplayName("회원 책 서비스 테스트")
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
  void 회원_책_등록_시_이미_등록된_책이면_기존_책_ID_반환() {
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
    assertThat(saved.getStatus()).isEqualTo(BookStatus.BEST);
  }

  @Test
  void 회원_책_등록_시_책_id가_null이면_책_생성_및_회원_책_등록() {
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
    assertThat(saved.getStatus()).isEqualTo(BookStatus.HIGH);
  }

  @Test
  void 회원_소유_책_목록_조회() {
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
    assertThat(s1.status()).isEqualTo(BookStatus.BEST.name());

    MemberBookSummary s2 = summaries.get(1);
    assertThat(s2.id()).isEqualTo(NEW_MEMBER_BOOK.getId());
    assertThat(s2.status()).isEqualTo(BookStatus.HIGH.name());
  }

  @Test
  void id_기반_회원_소유_책_목록_조회() {
    // given
    MemberBook mb1 = DEFAULT_MEMBER_BOOK;
    MemberBook mb2 = NEW_MEMBER_BOOK;
    ReadMemberBooksByIdQuery query = ReadMemberBooksByIdQuery.of(List.of(mb1.getId(), mb2.getId()));
    given(memberBookReader.readMemberBooksByBookIds(query.ids())).willReturn(List.of(mb1, mb2));
    given(bookPort.readBookSummaries(List.of(mb1.getBookId(), mb2.getBookId()))).willReturn(DEFAULT_BOOK_RESPONSES);


    // when
    MemberBooksResponse response = service.readMemberBooksByIdsProcess(query);

    // then
    then(memberBookReader).should().readMemberBooksByBookIds(query.ids());
    then(bookPort).should().readBookSummaries(List.of(DEFAULT_MEMBER_BOOK.getBookId(), NEW_MEMBER_BOOK.getBookId()));

    List<MemberBookSummary> summaries = response.books();
    assertThat(summaries).hasSize(2);

    MemberBookSummary s1 = summaries.get(0);
    assertThat(s1.id()).isEqualTo(DEFAULT_MEMBER_BOOK.getId());
    assertThat(s1.status()).isEqualTo(BookStatus.BEST.name());

    MemberBookSummary s2 = summaries.get(1);
    assertThat(s2.id()).isEqualTo(NEW_MEMBER_BOOK.getId());
    assertThat(s2.status()).isEqualTo(BookStatus.HIGH.name());
  }

  @Test
  void 회원_책_사용처_변경() {
    // given
    Long usageId = 1L;
    List<Long> ids = List.of(1L, 2L);
    given(memberBookReader.readMemberBooksByBookIds(ids)).willReturn(List.of(DEFAULT_MEMBER_BOOK, NEW_MEMBER_BOOK));
    ChangeMemberBookUsageCommand command = ChangeMemberBookUsageCommand.of(usageId, ids);

    // when
    service.changeMemberBookUsageProcess(command);

    // then
    assertThat(DEFAULT_MEMBER_BOOK.getUsageId()).isEqualTo(usageId);
    assertThat(NEW_MEMBER_BOOK.getUsageId()).isEqualTo(usageId);
    then(bookPort).shouldHaveNoInteractions();
  }

  @Test
  void 회원_책_사용처_변경_시_같은_사용처면_변경에서_제외() {
    // given
    Long usageId = 1L;
    List<Long> ids = List.of(1L, 2L);
    MemberBook nullUsage = mock(MemberBook.class);
    given(nullUsage.getUsageId()).willReturn(null);
    MemberBook alreadySameUsage = mock(MemberBook.class);
    given(alreadySameUsage.getUsageId()).willReturn(usageId);
    given(memberBookReader.readMemberBooksByBookIds(ids)).willReturn(List.of(nullUsage, alreadySameUsage));
    ChangeMemberBookUsageCommand command = ChangeMemberBookUsageCommand.of(usageId, ids);

    // when
    service.changeMemberBookUsageProcess(command);

    // then
    assertThat(DEFAULT_MEMBER_BOOK.getUsageId()).isEqualTo(usageId);
    then(memberBookReader).should().readMemberBooksByBookIds(ids);
    then(nullUsage).should(times(1)).updateUsageId(usageId);
    then(alreadySameUsage).should(never()).updateUsageId(usageId);
    then(bookPort).shouldHaveNoInteractions();
  }

  @Test
  void 회원_책_사용처_변경_시_다른_사용처가_존재하면_예외() {
    // given
    Long usageId = 1L;
    List<Long> ids = List.of(1L, 2L);
    given(memberBookReader.readMemberBooksByBookIds(ids)).willReturn(List.of(DEFAULT_MEMBER_BOOK, DIFF_IN_TRADE_BOOK));
    given(bookPort.readBookSummary(DIFF_IN_TRADE_BOOK.getBookId())).willReturn(DEFAULT_BOOK_RESPONSE);
    ChangeMemberBookUsageCommand command = ChangeMemberBookUsageCommand.of(usageId, ids);

    // when & then
    assertThatThrownBy(() -> service.changeMemberBookUsageProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.MEMBER_BOOK_ALREADY_USE.getMessage(), DIFF_IN_TRADE_BOOK.getUsageId(), DEFAULT_BOOK_RESPONSE.title());
  }

  @Test
  void 회원_책_삭제() {
    // given
    MemberBook mb = DEFAULT_MEMBER_BOOK;
    RemoveMemberBookCommand command = RemoveMemberBookCommand.of(MEMBER_ID, DEFAULT_MEMBER_BOOK.getId());
    given(memberBookReader.readMemberBookById(mb.getId())).willReturn(mb);

    // when
    service.removeMemberBookProcess(command);

    // then
    assertThat(mb.isRemove()).isTrue();
  }

  @Test
  void 회원_책_삭제_시_소유자가_아니면_예외가_발생한다() {
    // given
    MemberBook mb = DEFAULT_MEMBER_BOOK;
    RemoveMemberBookCommand command = RemoveMemberBookCommand.of(UUID.randomUUID(), DEFAULT_MEMBER_BOOK.getId());

    given(memberBookReader.readMemberBookById(DEFAULT_MEMBER_BOOK.getId())).willReturn(mb);

    // when & then
    assertThatThrownBy(() -> service.removeMemberBookProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.OBJECT_ACCESS_DENIED.getMessage());

    then(memberBookRepository).shouldHaveNoInteractions();
  }

  @Test
  void 회원_책_삭제_시_사용처가_있으면_예외가_발생한다() {
    // given
    RemoveMemberBookCommand command = RemoveMemberBookCommand.of(MEMBER_ID, 3L);
    MemberBook mb = SAME_IN_TRADE_BOOK;

    given(memberBookReader.readMemberBookById(SAME_IN_TRADE_BOOK.getId())).willReturn(mb);

    // when & then
    assertThatThrownBy(() -> service.removeMemberBookProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.UNREMOVABLE_MEMBER_BOOK.getMessage(), 1L);

    then(memberBookRepository).shouldHaveNoInteractions();
  }
}
