package com.bob.web.member.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberWishesResultFixture.DEFAULT_MEMBER_WISHES_RESULT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import com.bob.domain.member.usecase.MemberWishWriteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("회원 희망 도서 API 테스트")
@ExtendWith(MockitoExtension.class)
class MemberWishControllerTest {

  @InjectMocks
  private MemberWishController controller;

  @Mock
  private MemberWishWriteUseCase writeUseCase;

  @Mock
  private MemberWishReadUseCase readUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = standaloneSetup(controller).build();
  }

  @Test
  void 희망_도서_생성_기능_호출() throws Exception {
    // given
    String json = """
        {
          "isbn": "9788966264414",
          "title": "제목",
          "author": "작가",
          "description": "설명",
          "priceStandard": 10000,
          "cover": "https://image.jpg",
          "pubDate": "1970-01-01"
        }
        """;

    // when & then
    mvc.perform(post("/members/wishes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("CREATED"));

    verify(writeUseCase, times(1)).createMemberWishProcess(any(CreateMemberWishCommand.class));
  }

  @Test
  void 회원_희망_도서_목록_조회_성공() throws Exception {
    // given
    MemberWishesResult result = DEFAULT_MEMBER_WISHES_RESULT;
    given(readUseCase.readWishesProcess(any(ReadMemberWishesQuery.class))).willReturn(result);

    // when & then
    mvc.perform(get("/members/{memberId}/wishes", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wishes.length()").value(result.wishes().size()))
        .andExpect(jsonPath("$.wishes[0].id").value(result.wishes().get(0).id().intValue()))
        .andExpect(jsonPath("$.wishes[1].id").value(result.wishes().get(1).id().intValue()));

    verify(readUseCase, times(1)).readWishesProcess(any(ReadMemberWishesQuery.class));
  }
}
