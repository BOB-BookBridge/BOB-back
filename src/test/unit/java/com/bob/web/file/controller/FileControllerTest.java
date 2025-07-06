package com.bob.web.file.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.file.usecase.FileWriteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("파일 API 테스트")
@ExtendWith(MockitoExtension.class)
class FileControllerTest {

  @InjectMocks
  private FileController fileController;

  @Mock
  private FileWriteUseCase writeUseCase;

  @Mock
  private FileReadUseCase readUseCase;

  @Mock
  private FileModifyUseCase modifyUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = standaloneSetup(fileController).build();
  }

  @Test
  @DisplayName("파일 등록 API 호출 테스트")
  void 파일을_등록할_수_있다() throws Exception {
    String json = """
        {
          "domain": "POST",
          "referenceId": "123e4567-e89b-12d3-a456-426614174000",
          "fileNames": [
            "post/1.png",
            "post/2.jpg"
          ]
        }
        """;

    mvc.perform(post("/files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID)
        )
        .andExpect(status().isCreated());

    verify(writeUseCase, times(1)).registerFileProcess(any());
  }

  @Test
  @DisplayName("Presigned URL 조회 테스트")
  void 이미지_URL을_조회할_수_있다() throws Exception {
    String json = """
        {
          "domain": "POST",
          "contentTypes": ["image/jpeg", "image/png"]
        }
        """;

    mvc.perform(post("/files/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
        .andExpect(status().isOk());

    verify(writeUseCase, times(1)).generateFileUploadUrl(any());
  }

  @Test
  @DisplayName("파일 수정 API 호출 테스트")
  void 파일을_수정할_수_있다() throws Exception {
    String json = """
        {
          "domain": "POST",
          "referenceId": "123e4567-e89b-12d3-a456-426614174000",
          "fileNames": [
            "post/updated1.png",
            "post/updated2.jpg"
          ]
        }
        """;

    mvc.perform(put("/files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .header("X-Authentication-Id", "0197365f-8074-7d24-a332-0c5f1dbe9c59")
            .requestAttr("memberId", MEMBER_ID)
        )
        .andExpect(status().isOk());

    verify(modifyUseCase, times(1)).changeFileProcess(any());
  }
}
