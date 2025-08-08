package com.bob.domain.file.service.reader;

import static com.bob.domain.file.entity.type.FileDomain.POST;
import static com.bob.support.fixture.domain.FileFixture.defaultFile;
import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("파일 리더 테스트")
@ExtendWith(MockitoExtension.class)
class FileReaderTest {

  @InjectMocks
  private FileReader fileReader;

  @Mock
  private FileRepository fileRepository;

  @Test
  @DisplayName("도메인 ID를 통한 파일 조회 테스트")
  void domainId로_파일_목록을_조회한다() {
    // given
    String domainId = String.valueOf(1L);
    List<File> files = defaultFiles();
    given(fileRepository.findByDomainAndReferenceId(POST, domainId)).willReturn(files);

    // when
    List<File> result = fileReader.readFileByReferenceId(POST, domainId);

    // then
    assertThat(result).containsExactlyElementsOf(files);
    verify(fileRepository).findByDomainAndReferenceId(POST, domainId);
  }

  @Test
  @DisplayName("참조되지 않은 파일 목록 조회 테스트")
  void 참조되지_않은_파일_목록을_조회한다() {
    // given
    List<File> unusedFiles = List.of(
        defaultFile("file1.png", 0, null),
        defaultFile("file2.png", 0, null)
    );
    given(fileRepository.findByReferenceIdIsNull()).willReturn(unusedFiles);

    // when
    List<File> result = fileReader.readUnusedFiles();

    // then
    assertThat(result).containsExactlyElementsOf(unusedFiles);
    verify(fileRepository).findByReferenceIdIsNull();
  }

  @Test
  @DisplayName("파일 이름을 통한 파일 조회 테스트")
  void referenceId로_파일_목록을_조회한다() {
    // given
    String fileName = "post/name.png";
    File file = defaultFile("post/name.png", 0 ,"1");
    given(fileRepository.findByFileName(fileName)).willReturn(Optional.of(file));

    // when
    File result = fileReader.readFileByFileName(fileName).get();

    // then
    assertThat(result.getReferenceId()).isEqualTo(file.getReferenceId());
    verify(fileRepository).findByFileName(fileName);
  }
}