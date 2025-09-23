package com.bob.web.dummy.service;

import static com.bob.domain.post.entity.status.BookStatus.BEST;
import static com.bob.domain.post.entity.status.BookStatus.HIGH;
import static com.bob.domain.post.entity.status.BookStatus.LOW;
import static com.bob.domain.post.entity.status.BookStatus.MEDIUM;

import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.book.entity.Book;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.repository.CategoryRepository;
import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.post.repository.PostRepository;
import com.bob.global.utils.web.CookieUtils;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.web.dummy.command.CreateDummyManagerCommand;
import com.bob.web.dummy.command.CreateDummyPostCommand;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DummyService {

  private final MemberRepository memberRepository;
  private final ActivityAreaRepository areaRepository;
  private final PostRepository postRepository;
  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder encoder;

  public void createDummyManagerProcess(CreateDummyManagerCommand command, HttpServletResponse response) {
    Optional<Member> exist = memberRepository.findByEmail("manager@manager.com");
    if (exist.isPresent()) {
      Member manager = exist.get();
      String accessToken = jwtProvider.generateAccessToken(manager.getId().toString());
      CookieUtils.addCookie(response, "AUTHORIZATION", accessToken, 216000);
      return;
    }

    Member manager = memberRepository.save(command.toDummyManager(encoder.encode("manager")));
    areaRepository.save(command.toActivityArea(manager.getId()));
    createDummyPostProcess(manager.getId());

    String accessToken = jwtProvider.generateAccessToken(manager.getId().toString());
    CookieUtils.addCookie(response, "AUTHORIZATION", accessToken, 216000);
  }

  private void createDummyPostProcess(UUID memberId) {
    for (int idx = 0; idx < 5; idx++) {
      Book book = bookRepository.findById((long) (idx + 1)).get();
      Category category = categoryRepository.findById(CATEGORY_ID_LIST.get(idx)).get();

      postRepository.save(
          CreateDummyPostCommand.builder()
              .book(book)
              .category(category)
              .sellerId(memberId)
              .sellPrice(PRICE_LIST.get(idx))
              .description(DESCRIPTION_LIST.get(idx))
              .areaId(213)
              .build().toPost(book.getCover(), BOOK_STATUS_LIST.get(idx))
      );
    }
  }

  private static final List<Integer> CATEGORY_ID_LIST = List.of(20, 20, 28, 29, 28);
  private static final List<Integer> PRICE_LIST = List.of(24000, 25000, 6000, 7000, 4000);
  private static final List<BookStatus> BOOK_STATUS_LIST = List.of(BEST, BEST, MEDIUM, HIGH, LOW);
  private static final List<String> DESCRIPTION_LIST = List.of(
      "최상 상태로 거의 새 책과 같습니다. MySQL 8.0의 최신 기능을 배우고 싶은 백엔드 개발자에게 적극 추천합니다!",
      "보관만 했던 책이라 상태가 아주 좋습니다. MySQL 실전 가이드를 원하는 모든 개발자에게 적합합니다.",
      "중고 도서 특성상 약간의 사용감은 있지만, 전체적으로 양호한 상태입니다. 일본 특유의 섬세한 정서와 미스터리 장르의 매력을 동시에 느끼고 싶은 분께 권해드립니다. 퇴근 후 조용한 밤에 천천히 읽기 좋은 작품입니다.",
      "표지에 살짝의 스크래치가 있으나 내부 상태는 아주 양호합니다. 조각난 인물의 서사와 킬러로서의 성장 과정을 따라가는 밀도 높은 이야기. 구병모 작가의 팬은 물론, 강렬한 심리 묘사를 좋아하는 독자에게 꼭 추천드립니다.",
      "자국감이 다소 있고 일부 낙서가 있지만 내용 파악에 전혀 문제가 없습니다. 미국 문학의 고전이라 할 수 있는 이 작품은 청소년기의 방황과 외로움을 그려내며 지금도 많은 이들에게 공감을 줍니다. 저렴한 가격으로 명작을 경험해보세요."
  );
}
