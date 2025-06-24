package com.bob.domain.file.service;

import static com.bob.support.fixture.command.RegisterFileCommandFixture.defaultRegisterFileCommand;
import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.query.ReadMultiFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadSingleFileUploadUrlQuery;
import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;
import com.bob.domain.file.service.port.FileImagePort;
import com.bob.domain.file.service.reader.FileReader;
import com.bob.global.utils.image.ImageUtils;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("파일 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  @InjectMocks
  private FileService fileService;

  @Mock
  private FileRepository fileRepository;

  @Mock
  private FileReader fileReader;

  @Mock
  private FileImagePort imagePort;

  @DisplayName("파일 등록 - 성공 테스트")
  @Test
  void 파일을_등록할_수_있다() {
    // given
    RegisterFileCommand command = defaultRegisterFileCommand();

    // when
    fileService.registerFileProcess(command);

    // then
    then(fileRepository).should(times(1)).saveAll(any());
  }

  @DisplayName("파일 레퍼런스ID 수정 - 성공 테스트")
  @Test
  void 파일의_referenceId를_수정할_수_있다() {
    // given
    String oldReferenceId = "temp-reference-id";
    Long newReferenceId = 1L;
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(oldReferenceId, newReferenceId);

    List<File> files = defaultFiles(oldReferenceId);
    given(fileReader.readFileByReferenceId(oldReferenceId)).willReturn(files);

    // when
    fileService.modifyReferenceIdProcess(command);

    // then
    assertThat(files)
        .extracting(File::getReferenceId)
        .containsOnly(newReferenceId.toString());

    then(fileReader).should().readFileByReferenceId(oldReferenceId);
  }

  @DisplayName("단일 presigned URL 발급 테스트")
  @Test
  void 단일_presigned_url을_발급받을_수_있다() {
    // given
    ReadSingleFileUploadUrlQuery query = new ReadSingleFileUploadUrlQuery("post", "image/png");
    String expectedFileName = "post/" + UUID.randomUUID() + ".png";
    String expectedUrl = "https://mock-url.com/" + expectedFileName;

    given(imagePort.generateSingleFileUploadUrlProcess(anyString(), eq("image/png"))).willReturn(expectedUrl);

    // when
    ReadSingleFileUploadUrlResponse response = fileService.readSingleFileUploadUrl(query);

    // then
    assertThat(response.fileUploadUrl()).isEqualTo(expectedUrl);
    assertThat(response.fileName()).isNotBlank();
    then(imagePort).should().generateSingleFileUploadUrlProcess(anyString(), eq("image/png"));
  }

  @DisplayName("다중 presigned URL 발급 테스트")
  @Test
  void 여러_개의_presigned_url을_발급받을_수_있다() {
    // given
    List<String> contentTypes = List.of("image/png", "image/jpeg");
    ReadMultiFileUploadUrlQuery query = new ReadMultiFileUploadUrlQuery("post", contentTypes);

    List<String> fileNames = List.of("post/uuid1.png", "post/uuid2.jpg");
    List<String> urls = List.of(
        "https://mock-url.com/post/uuid1.png",
        "https://mock-url.com/post/uuid2.jpg"
    );

    given(imagePort.generateMultiFileUploadUrlsProcess(fileNames, contentTypes))
        .willReturn(urls);

    try (MockedStatic<ImageUtils> utils = mockStatic(ImageUtils.class)) {
      utils.when(() -> ImageUtils.generateImageFileNames(any(), eq(contentTypes)))
          .thenReturn(fileNames);

      // when
      ReadMultiFileUploadUrlResponse response = fileService.readMultiFileUploadUrl(query);

      // then
      assertThat(response.urls()).hasSize(2);
      assertThat(response.urls())
          .extracting("fileName")
          .containsExactlyElementsOf(fileNames);
      assertThat(response.urls())
          .extracting("fileUploadUrl")
          .containsExactlyElementsOf(urls);

      then(imagePort).should().generateMultiFileUploadUrlsProcess(fileNames, contentTypes);
    }
  }
}