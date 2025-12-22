package com.bob.core.file.application.port.in;

import static com.bob.support.fixture.file.domain.FileFixture.createFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.file.application.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.core.file.domain.File;
import com.bob.core.file.domain.repository.FileRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("파일 조회 테스트")
@ContainerTest
record FileReaderTest(FileReader fileReader, FileRepository fileRepository) {

    @Test
    void 도메인_ID_기반_파일_목록_조회() {
        String referenceId = "1";
        fileRepository.save(createFile("post/1.jpg", 0, referenceId));
        fileRepository.save(createFile("post/2.jpg", 1, referenceId));
        fileRepository.save(createFile("post/3.jpg", 2, referenceId));

        ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery("POST", referenceId);

        List<File> result = fileReader.readByDomainId(query);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(File::getReferenceId).containsOnly(referenceId);
    }

    @Test
    void 이름_기반_파일_조회() {
        File file = createFile("post/test.jpg", 0, "1");
        fileRepository.save(file);

        File result = fileReader.readByName("post/test.jpg");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("post/test.jpg");
    }

    @Test
    void 파일명으로_파일_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        String nonExistentName = "post/nonexistent.jpg";

        assertThatThrownBy(() -> fileReader.readByName(nonExistentName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("파일을 찾을 수 없습니다.");
    }

    @Test
    void 미사용_파일_목록_조회() {
        fileRepository.save(createFile("post/unused1.jpg", 0, null));
        fileRepository.save(createFile("post/unused2.jpg", 0, null));
        fileRepository.save(createFile("post/used.jpg", 0, "1"));

        List<File> result = fileReader.readUnusedFiles();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(File::getName).containsExactlyInAnyOrder("post/unused1.jpg", "post/unused2.jpg");
    }
}
