package com.bob.web.file.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
        )
        .andExpect(status().isCreated());

    verify(writeUseCase, times(1)).registerFileProcess(any());
  }

  @Test
  @DisplayName("단일 Presigned URL 조회 테스트")
  void 단일_이미지_URL을_조회할_수_있다() throws Exception {
    String json = """
        {
          "domain": "POST",
          "contentType": "image/png"
        }
        """;

    mvc.perform(get("/files/url")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
        .andExpect(status().isOk());

    verify(readUseCase, times(1)).readSingleFileUploadUrl(any());
  }

  @Test
  @DisplayName("복수 Presigned URL 조회 테스트")
  void 복수_이미지_URL을_조회할_수_있다() throws Exception {
    String json = """
        {
          "domain": "POST",
          "contentTypes": ["image/jpeg", "image/png"]
        }
        """;

    mvc.perform(get("/files/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
        .andExpect(status().isOk());

    verify(readUseCase, times(1)).readMultiFileUploadUrl(any());
  }
}
