package com.bob.domain.post.repository;

import static com.bob.support.fixture.domain.BookFixture.defaultBook;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory2;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.defaultPost;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.support.config.TestConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayName("PostRepository 테스트")
@Import(TestConfig.class)
@DataJpaTest
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @PersistenceContext
  private EntityManager em;

  private Post post;

  @BeforeEach
  void setUp() {
    Book book = bookRepository.save(defaultBook());
    Category category = categoryRepository.save(defaultCategory2());

    post = defaultPost(category, book, MEMBER_ID, EMD_AREA_ID);
    postRepository.save(post);
  }

  @Test
  @DisplayName("판매자 ID로 게시글 목록 조회 - 반환 테스트")
  void 판매자_ID로_게시글을_조회할_수_있다() {
    // when
    List<Post> result = postRepository.findAllBySellerId(MEMBER_ID);

    // then
    assertThat(result).hasSize(1);
    assertThat(result).extracting(Post::getSellerId).containsOnly(MEMBER_ID);
  }

  @Test
  @DisplayName("판매자 ID로 게시글 목록 조회 - empty 테스트")
  void 판매자_ID로_게시글이_없으면_빈_리스트를_반환한다() {
    // when
    List<Post> result = postRepository.findAllBySellerId(randomUUID());

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("게시글 조회수 증가 테스트")
  void 게시글_조회수를_증가시킬_수_있다() {
    // given
    Post post = postRepository.findById(this.post.getId()).get();
    Long postId = post.getId();
    int originalViewCount = post.getViewCount();

    // when
    postRepository.increaseViewCount(postId);
    em.flush();
    em.clear();

    Post updatedPost = postRepository.findById(postId).orElseThrow();

    // then
    assertThat(updatedPost.getViewCount()).isEqualTo(originalViewCount + 1);
  }
}
