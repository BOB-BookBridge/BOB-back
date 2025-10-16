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

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResult;
import com.bob.domain.post.usecase.PostDeleteUseCase;
import com.bob.domain.post.usecase.PostModifyUseCase;
import com.bob.domain.post.usecase.PostReadUseCase;
import com.bob.domain.post.usecase.PostWriteUseCase;
import com.bob.domain.trade.service.dto.response.TradeStatusMapResult;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.support.auth.WithAuthMember;
import java.util.Map;
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
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("게시글 API 테스트")
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

  @InjectMocks
  private PostController postController;

  @Mock
  private PostWriteUseCase writeUseCase;

  @Mock
  private PostReadUseCase readUseCase;

  @Mock
  private PostModifyUseCase modifyUseCase;

  @Mock
  private PostDeleteUseCase deleteUseCase;

  @Mock
  private TradeReadUseCase tradeReadUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(postController)
        .setCustomArgumentResolvers(
            new PageableHandlerMethodArgumentResolver(),
            new AuthenticationPrincipalArgumentResolver()
        )
        .build();
  }

  @Test
  void 게시글_생성_API_호출() throws Exception {
    // given
    String json = """
        {
          "categoryId": 1,
          "sellPrice": 10000,
          "bookStatus": "BEST",
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

    verify(writeUseCase, times(1)).createPostProcess(any());
  }

  @Test
  void 게시글_좋아요_등록_API_호출() throws Exception {
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(post("/posts/{postId}/favorite", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("CREATED"));

    verify(writeUseCase, times(1)).registerPostFavoriteProcess(any());
  }

  @Test
  void 게시글_좋아요_해제_API_호출() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(delete("/posts/{postId}/favorite", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    verify(deleteUseCase, times(1)).unregisterPostFavoriteProcess(any());
  }

  @Test
  void 게시글_목록_조회_API_호출() throws Exception {
    // given
    PostsResult result = new PostsResult(2L, DEFAULT_POST_SUMMARY());
    given(readUseCase.readFilteredPostsProcess(any(), any())).willReturn(result);

    // when & then
    mvc.perform(get("/posts")
            .param("key", "TITLE")
            .param("keyword", "")
            .param("emdId", "")
            .param("categoryId", "")
            .param("price", "")
            .param("postStatus", "READY")
            .param("bookStatus", "BEST")
            .param("sort", "RECENT")
            .param("page", "0")
            .param("size", "12")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.posts[0].postTitle").value("객체지향의 사실과 오해"))
        .andExpect(jsonPath("$.posts[1].postTitle").value("오브젝트"));

    verify(readUseCase, times(1)).readFilteredPostsProcess(any(), any());
  }

  @Test
  @WithAuthMember
  void 게시글_목록_조회_API_호출_시_로그인_상태면_거래_상태_매핑() throws Exception {
    // given
    PostsResult result = new PostsResult(2L, DEFAULT_POST_SUMMARY());
    TradeStatusMapResult tradeStatusResult = TradeStatusMapResult.of(Map.of());
    given(readUseCase.readFilteredPostsProcess(any(), any())).willReturn(result);
    given(tradeReadUseCase.readTradeStatusProcess(any())).willReturn(tradeStatusResult);

    // when & then
    mvc.perform(get("/posts")
            .param("key", "TITLE")
            .param("keyword", "")
            .param("emdId", "")
            .param("categoryId", "")
            .param("price", "")
            .param("postStatus", "READY")
            .param("bookStatus", "BEST")
            .param("sort", "RECENT")
            .param("page", "0")
            .param("size", "12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.posts[0].postTitle").value("객체지향의 사실과 오해"))
        .andExpect(jsonPath("$.posts[1].postTitle").value("오브젝트"));

    verify(readUseCase, times(1)).readFilteredPostsProcess(any(), any());
    verify(tradeReadUseCase, times(1)).readTradeStatusProcess(any());
  }

  @Test
  void 게시글_상세_조회_API_호출() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();
    PostDetailResponse response = DEFAULT_POST_DETAIL_RESPONSE(postId);
    given(readUseCase.readPostDetailProcess(any())).willReturn(response);

    // when & then
    mvc.perform(get("/posts/{postId}", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId").value(postId))
        .andExpect(jsonPath("$.sellPrice").value(10000))
        .andExpect(jsonPath("$.bookStatus").value("BEST"))
        .andExpect(jsonPath("$.postStatus").value("READY"))
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

    verify(readUseCase, times(1)).readPostDetailProcess(any());
  }

  @Test
  void 게시글_수정_API_호출() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();
    String json = """
        {
          "sellPrice": 12000,
          "bookStatus": "MEDIUM",
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

    verify(modifyUseCase, times(1)).changePostProcess(any());
  }

  @Test
  void 게시글_삭제_API_호출() throws Exception {
    // given
    Long postId = 1L;
    UUID memberId = UUID.randomUUID();

    // when & then
    mvc.perform(delete("/posts/{postId}", postId)
            .requestAttr("memberId", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    verify(deleteUseCase, times(1)).removePostProcess(any());
  }
}