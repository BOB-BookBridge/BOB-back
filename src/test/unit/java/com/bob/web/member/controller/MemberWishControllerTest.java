package com.bob.web.member.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
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
}
