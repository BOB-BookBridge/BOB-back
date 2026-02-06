package com.bob.core.post.domain;

import static com.bob.core.post.domain.Post.createPost;
import static com.bob.core.post.domain.status.Status.ACTIVE;
import static com.bob.core.post.domain.status.Status.DEACTIVATED;
import static com.bob.core.post.domain.status.TradeProgress.READY;
import static com.bob.core.post.domain.status.TradeProgress.RESERVED;
import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.post.domain.status.BookStatus;
import com.bob.support.fixture.post.domain.PostFixture;

@DisplayName("게시물 도메인 테스트")
class PostTest {

    @Test
    void 게시물_생성() {
        Integer categoryId = 21;
        int emdId = EMD_AREA_ID;
        Long bookId = 1L;
        String title = "제목";
        String description = "설명";
        String thumbnailUrl = "https://thumbnail.jpg";
        String bookStatus = "HIGH";
        UUID writerId = MEMBER_ID;
        Long writerBookId = 10L;
        Integer price = 30000;
        boolean wishOnly = false;

        Post post = createPost(
            categoryId, emdId, bookId, title, description, thumbnailUrl,
            bookStatus, writerId, writerBookId, price, wishOnly, List.of()
        );

        assertThat(post.getStatus()).isEqualTo(ACTIVE);
        assertThat(post.getCategoryId()).isEqualTo(categoryId);
        assertThat(post.getBookId()).isEqualTo(bookId);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getDescription()).isEqualTo(description);
        assertThat(post.getThumbnailUrl()).isEqualTo(thumbnailUrl);
        assertThat(post.getBookStatus()).isEqualTo(BookStatus.HIGH);
        assertThat(post.getWriterId()).isEqualTo(writerId);
        assertThat(post.getWriterBookId()).isEqualTo(writerBookId);
        assertThat(post.getPrice()).isEqualTo(price);
        assertThat(post.getTradeProgress()).isEqualTo(READY);
        assertThat(post.getRegistrationAreaId()).isEqualTo(emdId);
        assertThat(post.isWishOnly()).isEqualTo(wishOnly);
        assertThat(post.getViewCount()).isEqualTo(0);
        assertThat(post.getScrapCount()).isEqualTo(0);
        assertThat(post.getFavorites()).isEmpty();
        assertThat(post.getCreatedAt()).isNotNull();
    }

    @Test
    void 게시물_정보_수정() {
        Post post = PostFixture.createPost();

        String newBookStatus = "MEDIUM";
        String newDescription = "수정된 설명입니다";
        Boolean newWishOnly = true;

        post.updateInfo(newBookStatus, newDescription, newWishOnly, List.of());

        assertThat(post.getBookStatus()).isEqualTo(BookStatus.MEDIUM);
        assertThat(post.getDescription()).isEqualTo(newDescription);
        assertThat(post.isWishOnly()).isTrue();
    }

    @Test
    void 게시물_정보_부분_수정() {
        Post post = PostFixture.createPost();
        String originalDescription = post.getDescription();
        BookStatus originalBookStatus = post.getBookStatus();

        post.updateInfo(null, null, true, List.of());

        assertThat(post.getBookStatus()).isEqualTo(originalBookStatus);
        assertThat(post.getDescription()).isEqualTo(originalDescription);
        assertThat(post.isWishOnly()).isTrue();
    }

    @Test
    void 게시글_거래_진행_상태_수정() {
        Post post = PostFixture.createPost();

        post.updateTradeProgress(RESERVED);

        assertThat(post.getTradeProgress()).isEqualTo(RESERVED);
    }

    @Test
    void 게시글_활성화() {
        Post post = PostFixture.createPost();
        post.deactivate();

        post.activate();

        assertThat(post.getStatus()).isEqualTo(ACTIVE);
        assertThat(post.isActive()).isTrue();
    }

    @Test
    void 게시글_비활성화() {
        Post post = PostFixture.createPost();
        post.addFavorite(MEMBER_ID);
        post.addFavorite(OTHER_MEMBER_ID);

        post.deactivate();

        assertThat(post.getStatus()).isEqualTo(DEACTIVATED);
        assertThat(post.isActive()).isFalse();
        assertThat(post.getFavorites()).isEmpty();
    }

    @Test
    void 게시글_비활성화_시_활성_상태가_아니면_예외가_발생한다() {
        Post post = PostFixture.createPost();
        post.deactivate();

        assertThatThrownBy(post::deactivate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("활성 상태가 아닙니다.");
    }

    @Test
    void 게시글_찜_추가() {
        Post post = PostFixture.createPost();
        UUID memberId = MEMBER_ID;

        post.addFavorite(memberId);

        assertThat(post.getFavorites()).hasSize(1);
        assertThat(post.getFavorites().get(0).getMemberId()).isEqualTo(memberId);
    }

    @Test
    void 게시글_찜_제거() {
        Post post = PostFixture.createPost();
        UUID memberId = MEMBER_ID;
        post.addFavorite(memberId);

        post.removeFavorite(memberId);

        assertThat(post.getFavorites()).isEmpty();
    }

    @Test
    void 게시글_찜_여부_확인() {
        Post post = PostFixture.createPost();
        UUID memberId = MEMBER_ID;
        post.addFavorite(memberId);

        assertThat(post.isFavorite(memberId)).isTrue();
        assertThat(post.isFavorite(UUID.randomUUID())).isFalse();
    }

    @Test
    void 게시글_예약_여부_확인() {
        Post post = PostFixture.createPost();
        post.updateTradeProgress(RESERVED);

        assertThat(post.isReserved()).isTrue();

        post.updateTradeProgress(READY);

        assertThat(post.isReserved()).isFalse();
    }

    @Test
    void 게시글_활성화_여부_확인() {
        Post post = PostFixture.createPost();

        assertThat(post.isActive()).isTrue();

        post.deactivate();

        assertThat(post.isActive()).isFalse();
        assertThat(post.isDeactivated()).isTrue();
    }
}
