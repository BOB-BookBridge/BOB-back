package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.defaultPost;

import com.bob.domain.post.entity.PostFavorite;
import java.util.List;

public class PostFavoriteFixture {

  public static List<PostFavorite> DEFAULT_MOCK_POST_FAVORITES() {
    return List.of(
        PostFavorite.builder()
            .id(1L)
            .memberId(MEMBER_ID)
            .post(defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID))
            .build(),
        PostFavorite.builder()
            .id(2L)
            .memberId(MEMBER_ID)
            .post(defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID))
            .build()
    );
  }
}
