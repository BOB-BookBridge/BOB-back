package com.bob.infra.aws.adapter.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("FileImageAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class FileImageAdapterTest {

  @Mock
  private ImageUrlReadUseCase readUseCase;

  @InjectMocks
  private FileImageAdapter adapter;

  @Test
  @DisplayName("단일 presigned URL 생성 테스트")
  void 단일_이미지_업로드_URL을_생성할_수_있다() {
    // given
    String fileName = "profile/test.png";
    String contentType = "image/png";
    String expectedUrl = "https://example.com/upload";
    given(readUseCase.generateSingleImageUploadUrlProcess(fileName, contentType))
        .willReturn(expectedUrl);

    // when
    String result = adapter.generateSingleFileUploadUrlProcess(fileName, contentType);

    // then
    assertThat(result).isEqualTo(expectedUrl);
  }

  @Test
  @DisplayName("여러 개 presigned URL 생성 테스트")
  void 여러개의_이미지_업로드_URL을_생성할_수_있다() {
    // given
    List<String> fileNames = List.of("profile/a.jpg", "profile/b.jpg");
    List<String> contentTypes = List.of("image/jpeg", "image/jpeg");
    List<String> expectedUrls = List.of("url1", "url2");
    given(readUseCase.generateMultiImageUploadUrlProcess(fileNames, contentTypes))
        .willReturn(expectedUrls);

    // when
    List<String> result = adapter.generateMultiFileUploadUrlsProcess(fileNames, contentTypes);

    // then
    assertThat(result).isEqualTo(expectedUrls);
  }
}