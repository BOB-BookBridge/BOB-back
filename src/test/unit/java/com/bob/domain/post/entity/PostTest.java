package com.bob.domain.post.entity;

import static com.bob.support.fixture.domain.BookFixture.defaultBook;
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
  @DisplayName("일부 필드만 null이 아닐 때 해당 필드만 업데이트된다")
  void 일부_필드만_업데이트된다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

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
  @DisplayName("모든 필드가 null이면 아무 것도 변경되지 않는다")
  void 모든_필드가_null이면_변경되지_않는다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

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
  @DisplayName("모든 필드가 주어지면 모두 업데이트된다")
  void 모든_필드가_업데이트된다() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

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
  void 게시글_보류_상태_변경_테스트() {
    // given
    Post post = defaultPost(defaultCategory(), defaultBook(), MEMBER_ID, EMD_AREA_ID);

    // when
    post.updateIsWithhold(true);

    // then
    assertThat(post.isWithhold()).isTrue();
  }
}
