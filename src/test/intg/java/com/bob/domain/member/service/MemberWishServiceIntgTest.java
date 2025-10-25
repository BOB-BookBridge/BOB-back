package com.bob.domain.member.service;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.support.TestContainerSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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

  @Test
  void 회원_희망_도서_생성() {
    // given
    UUID memberId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca4");
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
    List<MemberWish> wishes = repository.findAllByMemberId(memberId);
    assertThat(wishes).hasSize(1);
    MemberWish wish = wishes.get(0);
    assertThat(wish.getMemberId()).isEqualTo(memberId);
  }
}
