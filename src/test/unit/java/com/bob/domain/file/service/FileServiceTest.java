package com.bob.domain.file.service;

import static com.bob.domain.file.entity.type.FileDomain.*;
import static com.bob.global.exception.response.ApplicationError.FILE_UNAUTHORIZED;
import static com.bob.support.fixture.command.ChangeFileCommandFixture.DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1;
import static com.bob.support.fixture.command.CreatePostCommandFixture.FILE_NAMES;
import static com.bob.support.fixture.command.RegisterFileCommandFixture.defaultRegisterFileCommand;
import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static com.bob.support.fixture.domain.FileFixture.otherFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.entity.type.FileDomain;
import com.bob.domain.file.repository.FileRepository;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.command.GenerateFileUploadUrlCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.service.port.FileImagePort;
import com.bob.domain.file.service.reader.FileReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.utils.image.ImageUtils;
import java.util.List;
import java.util.Optional;
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

  @DisplayName("도메인 ID로 파일 요약 조회 테스트")
  @Test
  void 도메인_ID로_파일을_요약_조회할_수_있다() {
    // given
    String referenceId = "post-1234";
    List<File> files = defaultFiles();
    given(fileReader.readFileByReferenceId(POST, referenceId)).willReturn(files);

    // when
    FilesResponse response = fileService.readFilesByDomainId(new ReadFilesWithDomainIdQuery(POST, referenceId));

    // then
    assertThat(response.summaries()).hasSize(files.size());
    assertThat(response.summaries())
        .extracting("fileName")
        .containsExactlyElementsOf(files.stream().map(File::getFileName).toList());
    then(fileReader).should().readFileByReferenceId(POST, referenceId);
  }

  @DisplayName("파일 변경 - 성공 테스트")
  @Test
  void 파일을_변경할_수_있다() {
    // given
    String referenceId = "1";
    List<File> existingFiles = defaultFiles();
    given(fileReader.readFileByReferenceId(POST, referenceId)).willReturn(existingFiles);

    // when
    fileService.changeFileProcess(DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1);

    // then
    then(fileRepository).should().deleteAll(existingFiles);
    then(fileRepository).should().saveAll(anyList());
  }

  @DisplayName("파일 변경 - 실패 테스트 (권한 없음)")
  @Test
  void 파일을_수정할_때_권한이_없으면_예외가_발생한다() {
    // given
    String referenceId = "1";
    given(fileReader.readFileByReferenceId(POST, referenceId)).willReturn(otherFiles());

    // when & then
    assertThatThrownBy(() -> fileService.changeFileProcess(DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(FILE_UNAUTHORIZED.getMessage());

    then(fileRepository).shouldHaveNoInteractions();
  }

  @DisplayName("파일 레퍼런스ID 수정 - 성공 테스트")
  @Test
  void 파일의_referenceId를_수정할_수_있다() {
    // given
    Long referenceId = 1L;
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(FILE_NAMES, referenceId.toString());

    List<File> mockFiles = defaultFiles();

    for (int i = 0; i < FILE_NAMES.size(); i++) {
      String refId = FILE_NAMES.get(i);
      File file = mockFiles.get(i);
      given(fileReader.readFileByFileName(refId)).willReturn(Optional.of(file));
    }

    // when
    fileService.modifyReferenceIdProcess(command);

    // then
    for (int i = 0; i < mockFiles.size(); i++) {
      File file = mockFiles.get(i);
      assertThat(file.getReferenceId()).isEqualTo(String.valueOf(referenceId));
      assertThat(file.getSequence()).isEqualTo(i);
    }

    then(fileReader).should(times(FILE_NAMES.size())).readFileByFileName(any(String.class));
  }

  @DisplayName("presigned URL 발급 테스트")
  @Test
  void presigned_url을_발급받을_수_있다() {
    // given
    List<String> contentTypes = List.of("image/png", "image/jpeg");
    GenerateFileUploadUrlCommand command = new GenerateFileUploadUrlCommand("post", contentTypes);

    List<String> fileNames = List.of("post/uuid1.png", "post/uuid2.jpg");
    List<String> urls = List.of(
        "https://mock-url.com/post/uuid1.png",
        "https://mock-url.com/post/uuid2.jpg"
    );

    given(imagePort.generateFileUploadUrlsProcess(fileNames, contentTypes))
        .willReturn(urls);

    try (MockedStatic<ImageUtils> utils = mockStatic(ImageUtils.class)) {
      utils.when(() -> ImageUtils.generateImageFileNames(any(), eq(contentTypes)))
          .thenReturn(fileNames);

      // when
      FileUploadUrlResponse response = fileService.generateFileUploadUrl(command);

      // then
      assertThat(response.urls()).hasSize(2);
      assertThat(response.urls())
          .extracting("fileName")
          .containsExactlyElementsOf(fileNames);
      assertThat(response.urls())
          .extracting("fileUploadUrl")
          .containsExactlyElementsOf(urls);

      then(imagePort).should().generateFileUploadUrlsProcess(fileNames, contentTypes);
    }
  }
}