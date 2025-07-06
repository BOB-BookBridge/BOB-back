package com.bob.domain.file.service;

import static com.bob.global.exception.response.ApplicationError.FILE_UNAUTHORIZED;
import static com.bob.global.utils.image.ImageDirectory.POST;
import static com.bob.support.fixture.command.ChangeFileCommandFixture.DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1;
import static com.bob.support.fixture.command.ChangeFileCommandFixture.DEFAULT_CHANGE_FILE_COMMAND_REF_ID_2;
import static com.bob.support.fixture.command.CreatePostCommandFixture.FILE_NAMES;
import static com.bob.support.fixture.command.RegisterFileCommandFixture.defaultRegisterFileCommand;
import static com.bob.support.fixture.domain.FileFixture.customRefIdFiles;
import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static com.bob.support.fixture.domain.FileFixture.otherFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.bob.domain.file.entity.File;
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
import com.bob.support.TestContainerSupport;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class FileServiceIntgTest extends TestContainerSupport {

  @Autowired
  private FileService fileService;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private FileReader fileReader;

  @MockitoBean
  private FileImagePort imagePort;

  @DisplayName("파일 등록 테스트")
  @Test
  void 파일을_등록할_수_있다() {
    // given
    RegisterFileCommand command = defaultRegisterFileCommand();

    // when
    fileService.registerFileProcess(command);

    // then
    List<String> fileNames = command.fileNames();

    fileNames.forEach(fileName -> {
      Optional<File> optionalFile = fileReader.readFileByFileName(fileName);
      assertThat(optionalFile).isPresent();

      File file = optionalFile.get();
      assertThat(file.getFileName()).isEqualTo(fileName);
    });
  }

  @DisplayName("도메인 ID로 파일 요약 조회 테스트")
  @Test
  void 도메인_ID로_파일을_요약_조회할_수_있다() {
    // given
    String referenceId = "1";
    List<File> files = customRefIdFiles(referenceId);
    fileRepository.saveAll(files);

    // when
    FilesResponse response = fileService.readFilesByDomainId(new ReadFilesWithDomainIdQuery(referenceId));

    // then
    assertThat(response.summaries()).hasSize(files.size());
    assertThat(response.summaries())
        .extracting("sequence")
        .containsExactlyElementsOf(files.stream().map(File::getSequence).toList());
    assertThat(response.summaries())
        .extracting("fileName")
        .containsExactlyElementsOf(files.stream().map(File::getFileName).toList());
  }

  @DisplayName("파일 변경 - 성공 테스트")
  @Test
  void 파일을_변경할_수_있다() {
    // given
    String referenceId = "1";
    List<File> oldFiles = defaultFiles();
    fileRepository.saveAll(oldFiles);

    // when
    fileService.changeFileProcess(DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1);

    // then
    List<File> newFiles = fileReader.readFileByReferenceId(referenceId);
    assertThat(newFiles).hasSize(2);
    assertThat(newFiles)
        .extracting(File::getFileName)
        .containsExactly("post/other1.png", "post/other2.jpg");
    assertThat(newFiles)
        .extracting(File::getSequence)
        .containsExactly(0, 1);
  }

  @DisplayName("파일 변경 - 실패 테스트(권한 없음)")
  @Test
  void 파일을_수정할_때_파일을_올린_사람이_아닌_경우_예외가_발생한다() {
    // given
    String referenceId = "2";
    List<File> files = otherFiles();
    fileRepository.saveAll(files);

    // when & then
    assertThatThrownBy(() -> fileService.changeFileProcess(DEFAULT_CHANGE_FILE_COMMAND_REF_ID_2))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(FILE_UNAUTHORIZED.getMessage());

    List<File> result = fileReader.readFileByReferenceId(referenceId);
    assertThat(result).hasSize(files.size());
  }

  @Test
  @DisplayName("파일 referenceId 변경 테스트")
  void 파일의_referenceId를_변경할_수_있다() {
    // given
    List<File> files = defaultFiles();
    fileRepository.saveAll(files);

    Long newReferenceId = 1L;
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(FILE_NAMES, newReferenceId.toString());

    // when
    fileService.modifyReferenceIdProcess(command);

    // then
    List<File> updated = fileReader.readFileByReferenceId(newReferenceId.toString());
    assertThat(updated)
        .extracting(File::getReferenceId)
        .containsOnly(newReferenceId.toString());
  }

  @Test
  @DisplayName("presigned URL 발급 테스트")
  void presigned_url을_발급받을_수_있다() {
    // given
    List<String> contentTypes = List.of("image/png", "image/jpeg");
    GenerateFileUploadUrlCommand command = new GenerateFileUploadUrlCommand("post", contentTypes);

    List<String> fileNames = List.of("post/uuid1.png", "post/uuid2.jpg");
    List<String> urls = List.of(
        "https://mock-url.com/post/uuid1.png",
        "https://mock-url.com/post/uuid2.jpg"
    );

    given(imagePort.generateFileUploadUrlsProcess(fileNames, contentTypes)).willReturn(urls);

    try (MockedStatic<ImageUtils> utils = org.mockito.Mockito.mockStatic(ImageUtils.class)) {
      utils.when(() -> ImageUtils.generateImageFileNames(POST, contentTypes)).thenReturn(fileNames);

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
    }
  }
}
