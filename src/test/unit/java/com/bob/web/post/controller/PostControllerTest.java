package com.bob.web.post.controller;

import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_SUMMARY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("게시글 API 테스트")
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

  @InjectMocks
  private PostController postController;

  @Mock
  private PostService postService;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(postController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  @DisplayName("게시글 생성 API 호출 테스트")
  void 게시글_생성_API를_호출할_수_있다() throws Exception {
    // given
    String json = """
        {
          "categoryId": 1,
          "sellPrice": 10000,
          "bookStatus": "최상",
          "postDescription": "Description",
          "book": {
            "isbn": "1111111111111",
            "title": "Title",
            "author": "Author",
            "description": "Book Description",
            "priceStandard": 20000,
            "cover": "https://image/1.jpg",
            "pubDate": "2025-05-25"
          }
        }
        """;

    // when & then
    mvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", UUID.randomUUID()))
        .andExpect(status().isCreated());

    verify(postService, times(1)).createPostProcess(any());
  }

  @Test
  @DisplayName("게시글 좋아요 API 호출 테스트")
  void 게시글_좋아요_API를_호출할_수_있다() throws Exception {
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(post("/posts/{postId}/favorite", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("CREATED"));

    verify(postService, times(1)).registerPostFavoriteProcess(any());
  }

  @Test
  @DisplayName("게시글 좋아요 해제 API 호출 테스트")
  void 게시글_좋아요_해제_API를_호출할_수_있다() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(delete("/posts/{postId}/favorite", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    verify(postService, times(1)).unregisterPostFavoriteProcess(any());
  }

  @Test
  @DisplayName("게시글 목록 필터 조회 API 호출 테스트")
  void 게시글_목록_필터_조회_API를_호출할_수_있다() throws Exception {
    // given
    PostsResponse response = new PostsResponse(2L, DEFAULT_POST_SUMMARY());
    given(postService.readFilteredPostsProcess(any(), any())).willReturn(response);

    // when & then
    mvc.perform(get("/posts")
            .param("key", "TITLE")
            .param("keyword", "객체지향")
            .param("emdId", "11010")
            .param("categoryId", "1")
            .param("price", "0")
            .param("postStatus", "미거래")
            .param("bookStatus", "최상")
            .param("sort", "RECENT")
            .param("page", "0")
            .param("size", "12")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.posts[0].postTitle").value("객체지향의 사실과 오해"))
        .andExpect(jsonPath("$.posts[1].postTitle").value("오브젝트"));

    verify(postService, times(1)).readFilteredPostsProcess(any(), any());
  }

  @Test
  @DisplayName("게시글 상세 조회 API 호출 테스트")
  void 게시글_상세_조회_API를_호출할_수_있다() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();
    PostDetailResponse response = DEFAULT_POST_DETAIL_RESPONSE(postId);
    given(postService.readPostDetailProcess(any())).willReturn(response);

    // when & then
    mvc.perform(get("/posts/{postId}", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId").value(postId))
        .andExpect(jsonPath("$.sellPrice").value(10000))
        .andExpect(jsonPath("$.bookStatus").value("BEST"))
        .andExpect(jsonPath("$.postStatus").value("거래 대기"))
        .andExpect(jsonPath("$.category").value(19))
        .andExpect(jsonPath("$.book.title").value("파과"))
        .andExpect(jsonPath("$.book.author").value("구병모"))
        .andExpect(jsonPath("$.book.priceStandard").value(12600))
        .andExpect(jsonPath("$.writer.nickname").value("booklover"))
        .andExpect(jsonPath("$.scrapCount").value(2))
        .andExpect(jsonPath("$.viewCount").value(24))
        .andExpect(jsonPath("$.isFavorite").value(true))
        .andExpect(jsonPath("$.isOwner").value(false))
        .andExpect(jsonPath("$.createdAt[0]").value(2024));

    verify(postService, times(1)).readPostDetailProcess(any());
  }

  @Test
  @DisplayName("게시글 수정 API 호출 테스트")
  void 게시글_수정_API를_호출할_수_있다() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();
    String json = """
        {
          "sellPrice": 12000,
          "bookStatus": "중",
          "description": "설명이 수정되었습니다."
        }
        """;

    // when & then
    mvc.perform(
            patch("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("memberId", memberId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("OK"));

    verify(postService, times(1)).changePostProcess(any());
  }

  @Test
  @DisplayName("게시글 삭제 API 호출 테스트")
  void 게시글_삭제_API를_호출할_수_있다() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(delete("/posts/{postId}", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    verify(postService, times(1)).removePostProcess(any());
  }
}