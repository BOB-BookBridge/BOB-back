package com.bob.domain.post.entity;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
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
  void 게시글_좋아요_생성() {
    // given
    Post post = defaultIdPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    // when
    PostFavorite postFavorite = PostFavorite.create(MEMBER_ID, post);

    // then
    assertThat(postFavorite).isNotNull();
    assertThat(postFavorite.getMemberId()).isEqualTo(MEMBER_ID);
    assertThat(postFavorite.getPost()).isEqualTo(post);
  }
}
