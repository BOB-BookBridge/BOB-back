package com.bob.domain.member.service;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.dto.command.DeleteMemberWishCommand;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import com.bob.support.TestContainerSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("회원 희망도서 서비스 통합 테스트")
@Transactional
@SpringBootTest
class MemberWishServiceIntgTest extends TestContainerSupport {

  @Autowired
  private MemberWishService service;

  @Autowired
  private MemberWishRepository repository;

  MemberWish wish1;
  MemberWish wish2;

  @BeforeEach
  void setup() {
    // schema.sql init 데이터 : bookId 1 = 자바의 정석, bookId 2 = 자바 ORM 표준 JPA 프로그래밍
    wish1 = createWish(UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4"), 1L);
    wish2 = createWish(UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4"), 2L);
    repository.save(wish1);
    repository.save(wish2);
  }

  @Test
  void 회원_희망_도서_생성() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4");
    List<MemberWish> wishes = repository.findAllByMemberId(memberId);
    assertThat(wishes).hasSize(2);
    CreateMemberWishCommand command = CreateMemberWishCommand.builder()
        .memberId(memberId)
        .isbn(DEFAULT_ISBN)
        .title("제목")
        .author("작가")
        .description("설명")
        .priceStandard(10000)
        .cover("https://image.url")
        .pubDate(LocalDate.now())
        .build();

    // when
    service.createMemberWishProcess(command);

    // then
    List<MemberWish> afterWishes = repository.findAllByMemberId(memberId);
    assertThat(afterWishes).hasSize(3);
  }

  @Test
  void 회원_희망_도서_목록_조회() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4");
    ReadMemberWishesQuery query = ReadMemberWishesQuery.of(memberId);

    // when
    MemberWishesResult result = service.readWishesProcess(query);

    // then
    assertThat(result).isNotNull();
    assertThat(result.wishes()).hasSize(2);
    List<String> titles = result.wishes().stream().map(MemberWishSummary::title).toList();
    assertThat(titles).containsExactlyInAnyOrder("자바의 정석", "자바 ORM 표준 JPA 프로그래밍");
  }

  @Test
  void 회원_희망_도서_삭제() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4");
    Long wishId = wish1.getId();
    DeleteMemberWishCommand command = DeleteMemberWishCommand.of(memberId, wishId);

    // when
    service.deleteWishProcess(command);

    // then
    Optional<MemberWish> removed = repository.findById(wishId);
    assertThat(removed).isNotPresent();
  }

  private MemberWish createWish(UUID memberId, Long bookId) {
    return MemberWish.create(memberId, bookId);
  }
}
