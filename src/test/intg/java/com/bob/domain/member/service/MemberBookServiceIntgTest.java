package com.bob.domain.member.service;

import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
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
import com.bob.support.TestContainerSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("회원 책 서비스 통합 테스트")
@Transactional
@SpringBootTest
class MemberBookServiceIntgTest extends TestContainerSupport {

  @Autowired
  private MemberBookService service;

  @Autowired
  private MemberBookRepository memberBookRepository;

  @Autowired
  private BookRepository bookRepository;

  @Test
  void 회원_책_등록_시_이미_등록된_책이면_기존_책_정보_사용() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca3");
    Optional<Book> findBook = bookRepository.findByIsbn13("9788966261208");
    assertThat(findBook).isPresent();

    Book exist = findBook.get();
    RegisterMemberBookCommand command = RegisterMemberBookCommand.builder()
        .memberId(memberId)
        .bookId(exist.getId())
        .status("BEST")
        .isbn(exist.getIsbn13())
        .title(exist.getTitle())
        .author(exist.getAuthor())
        .description(exist.getDescription())
        .priceStandard(exist.getPriceStandard())
        .cover(exist.getCover())
        .pubDate(exist.getPubDate())
        .build();

    // when
    service.registerMemberBookProcess(command);

    // then
    List<MemberBook> all = memberBookRepository.findByMemberId(memberId);
    assertThat(all).hasSize(1);

    MemberBook saved = all.get(0);
    assertThat(saved.getMemberId()).isEqualTo(memberId);
    assertThat(saved.getBookId()).isEqualTo(DEFAULT_REGISTER_MEMBER_BOOK_COMMAND.bookId());
    assertThat(saved.getStatus()).isEqualTo(BookStatus.BEST);
  }

  @Test
  void 회원_책_등록_시_책이_존재하지_않으면_책을_생성하고_해당_ID로_저장한다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca3");
    String unregisterBookIsbn = "9780000000002";
    Optional<Book> exist = bookRepository.findByIsbn13(unregisterBookIsbn);
    assertThat(exist).isNotPresent();

    RegisterMemberBookCommand command = RegisterMemberBookCommand.builder()
        .memberId(memberId)
        .bookId(null)
        .status("BEST")
        .isbn(unregisterBookIsbn)
        .title("신규 책")
        .author("임꺽정")
        .description("신규")
        .priceStandard(20000)
        .cover("http://image/new.jpg")
        .pubDate(LocalDate.of(2023, 12, 31))
        .build();

    // when
    service.registerMemberBookProcess(command);

    // then
    List<MemberBook> all = memberBookRepository.findByMemberId(memberId);
    assertThat(all).hasSize(1);

    MemberBook saved = all.get(0);
    assertThat(saved.getMemberId()).isEqualTo(memberId);
    assertThat(saved.getBookId()).isNotNull();
    assertThat(saved.getStatus()).isEqualTo(BookStatus.BEST);

    Optional<Book> newBook = bookRepository.findByIsbn13(unregisterBookIsbn);
    assertThat(newBook).isPresent();
  }

  @Test
  void 회원_소유_책_목록_정상_조회() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca3");
    MemberBook mb1 = memberBookRepository.save(MemberBook.of(memberId, 1L, "BEST"));
    MemberBook mb2 = memberBookRepository.save(MemberBook.of(memberId, 2L, "HIGH"));

    // when
    MemberBooksResponse response = service.readMemberBooksProcess(ReadMemberBooksQuery.of(memberId));

    // then
    List<MemberBookSummary> summaries = response.books();
    assertThat(summaries).hasSize(2);

    MemberBookSummary s1 = summaries.get(0);
    assertThat(s1.id()).isEqualTo(mb1.getId());
    assertThat(s1.status()).isEqualTo(BookStatus.BEST.name());
    assertThat(s1.title()).isEqualTo("자바의 정석");

    MemberBookSummary s2 = summaries.get(1);
    assertThat(s2.id()).isEqualTo(mb2.getId());
    assertThat(s2.status()).isEqualTo(BookStatus.HIGH.name());
    assertThat(s2.title()).isEqualTo("자바 ORM 표준 JPA 프로그래밍");
  }

  @Test
  void id_기반_회원_소유_책_목록_정상_조회() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    MemberBook mb1 = memberBookRepository.save(MemberBook.of(memberId, 1L, "BEST"));
    MemberBook mb2 = memberBookRepository.save(MemberBook.of(memberId, 2L, "HIGH"));
    ReadMemberBooksByIdQuery query = ReadMemberBooksByIdQuery.of(List.of(mb1.getId(), mb2.getId()));

    // when
    MemberBooksResponse response = service.readMemberBooksByIdsProcess(query);

    // then
    List<MemberBookSummary> summaries = response.books();
    assertThat(summaries).hasSize(2);

    MemberBookSummary s1 = summaries.get(0);
    assertThat(s1.id()).isEqualTo(mb1.getId());
    assertThat(s1.status()).isEqualTo(BookStatus.BEST.name());
    assertThat(s1.title()).isEqualTo("자바의 정석");

    MemberBookSummary s2 = summaries.get(1);
    assertThat(s2.id()).isEqualTo(mb2.getId());
    assertThat(s2.status()).isEqualTo(BookStatus.HIGH.name());
    assertThat(s2.title()).isEqualTo("자바 ORM 표준 JPA 프로그래밍");
  }

  @Test
  void 회원_책_사용처_변경() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    MemberBook mb1 = memberBookRepository.save(MemberBook.of(memberId, 1L, "BEST"));
    MemberBook mb2 = memberBookRepository.save(MemberBook.of(memberId, 2L, "HIGH"));

    Long usageId = 1L;
    List<Long> ids = List.of(mb1.getId(), mb2.getId());
    ChangeMemberBookUsageCommand command = ChangeMemberBookUsageCommand.of(memberId, usageId, ids, false);

    // when
    service.changeMemberBookUsageProcess(command);

    // then
    MemberBook updated1 = memberBookRepository.findById(mb1.getId()).orElseThrow();
    MemberBook updated2 = memberBookRepository.findById(mb2.getId()).orElseThrow();
    assertThat(updated1.getUsageId()).isEqualTo(usageId);
    assertThat(updated2.getUsageId()).isEqualTo(usageId);
  }

  @Test
  void 회원_소유_책_삭제() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    MemberBook mb = memberBookRepository.save(MemberBook.of(memberId, 1L, "BEST"));
    Long memberBookId = mb.getId();
    assertThat(mb.isRemove()).isFalse();

    // when
    service.removeMemberBookProcess(RemoveMemberBookCommand.of(memberId, memberBookId));

    // then
    Optional<MemberBook> updated = memberBookRepository.findById(memberBookId);
    assertThat(updated).isPresent();
    assertThat(updated.get().isRemove()).isTrue();
  }
}
