package com.bob.web.dummy.service;

import static com.bob.domain.post.entity.status.BookStatus.BEST;
import static com.bob.domain.post.entity.status.BookStatus.HIGH;
import static com.bob.domain.post.entity.status.BookStatus.LOW;
import static com.bob.domain.post.entity.status.BookStatus.MEDIUM;

import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.category.entity.Category;
import com.bob.domain.category.repository.CategoryRepository;
import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.domain.post.entity.status.BookStatus;
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

    String accessToken = jwtProvider.generateAccessToken(manager.getId().toString());
    CookieUtils.addCookie(response, "AUTHORIZATION", accessToken, 216000);
  }
}
