package com.bob.core.application.file.port.in;

import static com.bob.core.domain.file.type.FileDomain.POST;
import static com.bob.global.exception.response.ApplicationError.FILE_ACCESS_DENIED;
import static com.bob.support.fixture.file.domain.FileFixture.createFile;
import static com.bob.support.fixture.file.dto.command.MappingFileReferencesCommandFixture.createMappingFileReferencesCommand;
import static com.bob.support.fixture.file.dto.command.UpdateFilesCommandFixture.createUpdateFilesCommand;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.domain.file.File;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("파일 수정 테스트")
@ContainerTest
record FileModifierTest(FileModifier fileModifier, FileRepository fileRepository) {

    @Test
    void 파일_목록_수정() {
        String referenceId = "1";
        fileRepository.save(createFile("post/1.jpg", 0, referenceId));
        fileRepository.save(createFile("post/2.jpg", 1, referenceId));
        fileRepository.save(createFile("post/3.jpg", 2, referenceId));
        var command = createUpdateFilesCommand(referenceId);

        List<File> result = fileModifier.updateFiles(command);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(File::getName).containsExactly("post/other1.png", "post/other2.jpg");
        assertThat(result).extracting(File::getSequence).containsExactly(0, 1);
        assertThat(result).allMatch(file -> referenceId.equals(file.getReferenceId()));
    }

    @Test
    void 파일_목록_수정_시_작성자가_아니면_사용자_예외가_발생한다() {
        String referenceId = "2";
        File file1 = createFile("post/1.jpg", 0, referenceId);
        ReflectionTestUtils.setField(file1, "uploader", OTHER_MEMBER_ID);
        fileRepository.save(file1);

        var command = createUpdateFilesCommand(referenceId);

        assertThatThrownBy(() -> fileModifier.updateFiles(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(FILE_ACCESS_DENIED.getMessage());
    }

    @Test
    void 파일_참조_ID_매핑() {
        fileRepository.save(createFile("post/1.jpg", 0, "1"));
        fileRepository.save(createFile("post/2.jpg", 1, "1"));
        fileRepository.save(createFile("post/3.jpg", 2, "1"));

        String referenceId = "999";
        var command = createMappingFileReferencesCommand(referenceId);

        fileModifier.mappingReferences(command);

        List<File> found = fileRepository.findByDomainAndReferenceId(POST, referenceId);

        assertThat(found).hasSize(3);
    }

    @Test
    void 파일_참조_ID_매핑_시_일부_파일만_존재해도_정상_처리() {
        fileRepository.save(createFile("post/1.jpg", 0, "1"));

        String newReferenceId = "999";
        var command = createMappingFileReferencesCommand(List.of("post/1.jpg", "post/nonexistent.jpg"), newReferenceId);

        fileModifier.mappingReferences(command);

        List<File> updated = fileRepository.findByDomainAndReferenceId(POST, newReferenceId);
        assertThat(updated).hasSize(1);
        assertThat(updated.get(0).getReferenceId()).isEqualTo(newReferenceId);
    }
}
