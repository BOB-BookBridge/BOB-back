package com.bob.domain.file.service;

import static com.bob.global.utils.image.ImageDirectory.POST;
import static com.bob.support.fixture.command.RegisterFileCommandFixture.defaultRegisterFileCommand;
import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import com.bob.domain.file.service.dto.query.ReadFileUploadUrlQuery;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.service.port.FileImagePort;
import com.bob.domain.file.service.reader.FileReader;
import com.bob.global.utils.image.ImageUtils;
import com.bob.support.TestContainerSupport;
import com.bob.support.redis.RedisContainerConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Import(RedisContainerConfig.class)
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

  @Test
  @DisplayName("파일 등록 테스트")
  void 파일을_등록할_수_있다() {
    // given
    RegisterFileCommand command = defaultRegisterFileCommand();

    // when
    fileService.registerFileProcess(command);

    // then
    List<File> result = fileReader.readFileByReferenceId(command.referenceId().toString());
    assertThat(result).hasSize(command.fileNames().size());
    assertThat(result)
        .allSatisfy(file -> assertThat(file.getReferenceId()).isEqualTo(command.referenceId()));
  }

  @DisplayName("도메인 ID로 파일 요약 조회 테스트")
  @Test
  void 도메인_ID로_파일을_요약_조회할_수_있다() {
    // given
    String referenceId = "post-1234";
    List<File> files = defaultFiles(referenceId);
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

  @Test
  @DisplayName("파일 referenceId 변경 테스트")
  void 파일의_referenceId를_변경할_수_있다() {
    // given
    String oldReferenceId = "temp-ref-id";
    List<File> files = defaultFiles(oldReferenceId);
    fileRepository.saveAll(files);

    Long newReferenceId = 1L;
    ModifyReferenceIdCommand command = new ModifyReferenceIdCommand(oldReferenceId, newReferenceId);

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
    ReadFileUploadUrlQuery query = new ReadFileUploadUrlQuery("post", contentTypes);

    List<String> fileNames = List.of("post/uuid1.png", "post/uuid2.jpg");
    List<String> urls = List.of(
        "https://mock-url.com/post/uuid1.png",
        "https://mock-url.com/post/uuid2.jpg"
    );

    given(imagePort.generateFileUploadUrlsProcess(fileNames, contentTypes)).willReturn(urls);

    try (MockedStatic<ImageUtils> utils = org.mockito.Mockito.mockStatic(ImageUtils.class)) {
      utils.when(() -> ImageUtils.generateImageFileNames(POST, contentTypes)).thenReturn(fileNames);

      // when
      FileUploadUrlResponse response = fileService.readFileUploadUrl(query);

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
