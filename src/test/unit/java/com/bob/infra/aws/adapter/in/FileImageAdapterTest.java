package com.bob.infra.aws.adapter.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.infra.aws.service.usecase.FileDeleteUseCase;
import com.bob.infra.aws.service.usecase.FileReadUseCase;
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

  @InjectMocks
  private FileImageAdapter adapter;

  @Mock
  private FileReadUseCase readUseCase;

  @Mock
  private FileDeleteUseCase deleteUseCase;

  @Test
  @DisplayName("presigned URL 생성 테스트")
  void 이미지_업로드_URL을_생성할_수_있다() {
    // given
    List<String> fileNames = List.of("profile/a.jpg", "profile/b.jpg");
    List<String> contentTypes = List.of("image/jpeg", "image/jpeg");
    List<String> expectedUrls = List.of("url1", "url2");
    given(readUseCase.generateFileUploadUrlProcess(fileNames, contentTypes)).willReturn(expectedUrls);

    // when
    List<String> result = adapter.generateFileUploadUrlsProcess(fileNames, contentTypes);

    // then
    assertThat(result).isEqualTo(expectedUrls);
  }

  @Test
  @DisplayName("파일 삭제 테스트")
  void 사용되지_않은_파일들을_삭제할_수_있다() {
    // given
    List<String> fileNames = List.of("file1.jpg", "file2.jpg");

    // when
    adapter.removeUnusedFilesProcess(fileNames);

    // then
    verify(deleteUseCase).removeFilesProcess(fileNames);
  }
}