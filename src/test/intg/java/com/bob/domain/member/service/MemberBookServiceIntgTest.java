package com.bob.domain.member.service;

import static com.bob.support.fixture.command.RegisterMemberBookCommandFixture.DEFAULT_REGISTER_MEMBER_BOOK_COMMAND;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
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

@DisplayName("회원 도서 서비스 통합 테스트")
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
  void 회원_도서_정상_등록_이미_등록된_도서() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
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
    assertThat(saved.getStatus().name()).isEqualTo(DEFAULT_REGISTER_MEMBER_BOOK_COMMAND.status());
  }

  @Test
  void 도서ID가_null이면_신규도서를_생성하고_해당_ID로_저장한다() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
    String unregisterBookIsbn = "9780000000002";
    Optional<Book> exist = bookRepository.findByIsbn13(unregisterBookIsbn);
    assertThat(exist).isNotPresent();

    RegisterMemberBookCommand command = RegisterMemberBookCommand.builder()
        .memberId(memberId)
        .bookId(null)
        .status("BEST")
        .isbn(unregisterBookIsbn)
        .title("신규도서")
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
    assertThat(saved.getStatus().name()).isEqualTo("BEST");

    Optional<Book> newBook = bookRepository.findByIsbn13(unregisterBookIsbn);
    assertThat(newBook).isPresent();
  }
}
