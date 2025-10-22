package com.bob.web.member.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberBooksResponseFixture.DEFAULT_MEMBER_BOOKS_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("회원 책장 API 테스트")
@ExtendWith(MockitoExtension.class)
class MemberBookControllerTest {

  @InjectMocks
  private MemberBookController controller;

  @Mock
  private MemberBookWriteUseCase writeUseCase;

  @Mock
  private MemberBookReadUseCase readUseCase;

  @Mock
  private MemberBookRemoveUseCase removeUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = standaloneSetup(controller).build();
  }

  @Test
  void 회원_도서_등록_기능_호출() throws Exception {
    // given
    String json = """
        {
          "status": "BEST",
          "isbn": "9781234567890",
          "title": "테스트책",
          "author": "홍길동",
          "description": "설명",
          "priceStandard": 15000,
          "cover": "http://image.url/cover.jpg",
          "pubDate": "2024-01-02"
        }
        """;

    // when & then
    mvc.perform(post("/members/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("CREATED"));

    verify(writeUseCase, times(1)).registerMemberBookProcess(any(RegisterMemberBookCommand.class));
  }

  @Test
  void 회원_책장_조회_기능_호출() throws Exception {
    // given
    given(readUseCase.readMemberBooksProcess(any(ReadMemberBooksQuery.class))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    int expectedSize = DEFAULT_MEMBER_BOOKS_RESPONSE.bookcase().size();
    var first = DEFAULT_MEMBER_BOOKS_RESPONSE.bookcase().get(0);

    // when & then
    mvc.perform(get("/members/{memberId}/books", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookcase.length()").value(expectedSize))
        .andExpect(jsonPath("$.bookcase[0].id").value(first.id().intValue()))
        .andExpect(jsonPath("$.bookcase[0].title").value(first.title()));

    verify(readUseCase, times(1)).readMemberBooksProcess(any(ReadMemberBooksQuery.class));
  }

  @Test
  void 회원_책장_조회_require_포함_기능_호출() throws Exception {
    // given
    given(readUseCase.readMemberBooksProcess(any(ReadMemberBooksQuery.class))).willReturn(DEFAULT_MEMBER_BOOKS_RESPONSE);

    int expectedSize = DEFAULT_MEMBER_BOOKS_RESPONSE.bookcase().size();
    var first = DEFAULT_MEMBER_BOOKS_RESPONSE.bookcase().get(0);

    // when & then
    mvc.perform(get("/members/{memberId}/books", MEMBER_ID)
            .param("require", "1")
            .param("require", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookcase.length()").value(expectedSize))
        .andExpect(jsonPath("$.bookcase[0].id").value(first.id().intValue()))
        .andExpect(jsonPath("$.bookcase[0].title").value(first.title()));

    verify(readUseCase, times(1)).readMemberBooksProcess(any(ReadMemberBooksQuery.class));
  }

  @Test
  void 회원_소유_도서_삭제_기능_호출() throws Exception {
    // given
    long memberBookId = 1L;

    // when & then
    mvc.perform(delete("/members/books/{memberBookId}", memberBookId)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    verify(removeUseCase, times(1)).removeMemberBookProcess(any(RemoveMemberBookCommand.class));
  }
}