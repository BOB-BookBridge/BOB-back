package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse.BookInfo;
import com.bob.domain.post.service.dto.response.PostFavoritesResponse;
import com.bob.domain.post.service.dto.response.PostSummary;
import com.bob.domain.post.service.port.view.PostMemberView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PostResponseFixture {

  public static final PostSummary FIRST_POST = PostSummary.builder()
      .postId(1L)
      .postTitle("객체지향의 사실과 오해")
      .postStatus("READY")
      .sellPrice(10000)
      .thumbnailUrl("http://thumbnail1.com")
      .bookStatus(BookStatus.BEST.name())
      .createdAt(LocalDateTime.now())
      .build();

  public static final PostSummary SECOND_POST = PostSummary.builder()
      .postId(2L)
      .postTitle("오브젝트")
      .postStatus("READY")
      .sellPrice(15000)
      .thumbnailUrl("http://thumbnail2.com")
      .bookStatus(BookStatus.LOW.name())
      .createdAt(LocalDateTime.now())
      .build();

  public static PostDetailResponse DEFAULT_POST_DETAIL_RESPONSE(Long postId) {
    return PostDetailResponse.builder()
        .postId(postId)
        .sellerId(MEMBER_ID)
        .sellerBookId(DEFAULT_MEMBER_BOOK().getId())
        .sellPrice(10000)
        .bookStatus("BEST")
        .postStatus("READY")
        .category(19)
        .book(BookInfo.builder()
            .title("파과")
            .author("구병모")
            .description("파과는 청부살인을 업으로 삼아오던 60대 여성 킬러에 관한 이야기이다...")
            .priceStandard(12600)
            .pubDate("2018-04-16")
            .build())
        .description("책 상태 양호하고 밑줄 없음")
        .writer(PostMemberView.builder()
            .memberId(MEMBER_ID)
            .nickname("booklover")
            .emdId(309)
            .profileUrl("https://s3.bucket.com/default.jpg")
            .interests(List.of())
            .wishes(List.of())
            .build())
        .scrapCount(2)
        .viewCount(24)
        .isFavorite(true)
        .isOwner(false)
        .wishOnly(false)
        .createdAt(LocalDateTime.of(2024, 3, 29, 12, 0))
        .build();
  }

  public static PostDetailResponse CUSTOM_POST_DETAIL_RESPONSE(Long postId, UUID memberId, String status) {
    return PostDetailResponse.builder()
        .postId(postId)
        .status(status)
        .sellerId(memberId)
        .sellerBookId(1L)
        .sellPrice(7000)
        .bookStatus("LOW")
        .postStatus("IN_PROGRESS")
        .category(19)
        .book(BookInfo.builder()
            .title("파과")
            .author("구병모")
            .description("파과는 청부살인을 업으로 삼아오던 60대 여성 킬러에 관한 이야기이다...")
            .priceStandard(12600)
            .pubDate("2018-04-16")
            .build())
        .description("책 상태 양호하고 밑줄 없음")
        .writer(PostMemberView.builder()
            .memberId(memberId)
            .nickname("bookhater")
            .emdId(213)
            .profileUrl("https://s3.bucket.com/default.jpg")
            .interests(List.of())
            .wishes(List.of())
            .build())
        .scrapCount(2)
        .viewCount(24)
        .isFavorite(true)
        .isOwner(false)
        .wishOnly(false)
        .createdAt(LocalDateTime.of(2024, 3, 29, 12, 0))
        .build();
  }

  public static List<PostSummary> DEFAULT_POST_SUMMARY() {
    return List.of(FIRST_POST, SECOND_POST);
  }

  public static PostFavoritesResponse DEFAULT_FAVORITE_RESPONSE() {
    return new PostFavoritesResponse(2L, DEFAULT_POST_SUMMARY());
  }
}
