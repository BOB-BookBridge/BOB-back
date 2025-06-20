package com.bob.domain.post.entity;

import static com.bob.support.fixture.domain.BookFixture.defaultBook;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.defaultIdPost;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostFavorite 도메인 테스트")
class PostFavoriteTest {

  @Test
  @DisplayName("PostFavorite 생성 - 정상 테스트")
  void PostFavorite를_정상적으로_생성할_수_있다() {
    // given
    Post post = defaultIdPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

    // when
    PostFavorite postFavorite = PostFavorite.create(MEMBER_ID, post);

    // then
    assertThat(postFavorite).isNotNull();
    assertThat(postFavorite.getMemberId()).isEqualTo(MEMBER_ID);
    assertThat(postFavorite.getPost()).isEqualTo(post);
  }
}
