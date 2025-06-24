package com.bob.domain.file.service.reader;

import static com.bob.support.fixture.domain.FileFixture.defaultFiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.bob.domain.file.entity.File;
import com.bob.domain.file.repository.FileRepository;
import java.util.List;
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
  @DisplayName("referenceId로 파일 목록을 조회할 수 있다")
  void referenceId로_파일_목록을_조회한다() {
    // given
    String referenceId = "temp-uuid";
    List<File> files = defaultFiles(referenceId);
    given(fileRepository.findByReferenceId(referenceId)).willReturn(files);

    // when
    List<File> result = fileReader.readFileByReferenceId(referenceId);

    // then
    assertThat(result).containsExactlyElementsOf(files);
    verify(fileRepository).findByReferenceId(referenceId);
  }
}