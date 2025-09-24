package com.bob.domain.post.entity;

import static com.bob.domain.post.entity.status.Status.WITHHELD;
import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;
import static com.bob.support.fixture.domain.EmdAreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.PostFixture.defaultPost;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post 도메인 테스트")
class PostTest {

  @Test
  void 게시글_일부_수정() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    Integer newSellPrice = 8000;
    String newBookStatus = null;
    String newDescription = "커피 자국 있음";

    // when
    post.updateOptionalFields(newSellPrice, newBookStatus, newDescription);

    // then
    assertThat(post.getSellPrice()).isEqualTo(newSellPrice);
    assertThat(post.getDescription()).isEqualTo(newDescription);
  }

  @Test
  void 게시글_수정_시_null_입력() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    Integer beforePrice = post.getSellPrice();
    String beforeStatus = post.getBookStatus().name();
    String beforeDescription = post.getDescription();

    // when
    post.updateOptionalFields(null, null, null);

    // then
    assertThat(post.getSellPrice()).isEqualTo(beforePrice);
    assertThat(post.getBookStatus().name()).isEqualTo(beforeStatus);
    assertThat(post.getDescription()).isEqualTo(beforeDescription);
  }

  @Test
  void 게시글_수정_모든_사항_변경() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    Integer newSellPrice = 6000;
    String newBookStatus = "LOW";
    String newDescription = "찢어진 페이지 있음";

    // when
    post.updateOptionalFields(newSellPrice, newBookStatus, newDescription);

    // then
    assertThat(post.getSellPrice()).isEqualTo(newSellPrice);
    assertThat(post.getBookStatus().name()).isEqualTo(newBookStatus);
    assertThat(post.getDescription()).isEqualTo(newDescription);
  }

  @Test
  void 게시글_보류_상태_변경() {
    // given
    Post post = defaultPost(defaultCategory(), DEFAULT_BOOK, MEMBER_ID, EMD_AREA_ID);

    // when
    post.updateStatus(WITHHELD);

    // then
    assertThat(post.getStatus()).isEqualTo(WITHHELD);
  }
}
