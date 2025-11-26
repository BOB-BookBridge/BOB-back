package com.bob.core.application.file.port.in;

import static com.bob.support.fixture.file.domain.FileFixture.createFile;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.file.port.out.FileStoragePort;
import com.bob.core.domain.file.File;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.config.BobTestConfiguration;

@DisplayName("파일 삭제 테스트")
@ContainerTest
record FileRemoverTest(FileRemover fileRemover, FileRepository fileRepository, FileStoragePort fileStoragePort) {

    @AfterEach
    void tearDown() {
        BobTestConfiguration.TestFileStoragePort testPort = (BobTestConfiguration.TestFileStoragePort)fileStoragePort;
        testPort.clearDeletedFiles();
    }

    @Test
    void 미사용_파일_삭제() {
        fileRepository.save(createFile("post/unused1.jpg", 0, null));
        fileRepository.save(createFile("post/unused2.jpg", 0, null));
        fileRepository.save(createFile("post/used.jpg", 0, "1"));

        fileRemover.removeUnusedFiles();

        List<File> remaining = (List<File>)fileRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getName()).isEqualTo("post/used.jpg");

        BobTestConfiguration.TestFileStoragePort testPort = (BobTestConfiguration.TestFileStoragePort)fileStoragePort;
        assertThat(testPort.getDeletedFiles()).containsExactlyInAnyOrder("post/unused1.jpg", "post/unused2.jpg");
    }

    @Test
    void 미사용_파일이_없으면_아무_작업도_수행하지_않음() {
        fileRepository.save(createFile("post/used.jpg", 0, "1"));

        fileRemover.removeUnusedFiles();

        BobTestConfiguration.TestFileStoragePort testPort = (BobTestConfiguration.TestFileStoragePort)fileStoragePort;
        assertThat(testPort.getDeletedFiles()).isEmpty();
        assertThat(fileRepository.findAll()).hasSize(1);
    }

    @Test
    void 모든_파일이_미사용_파일이면_모두_삭제됨() {
        fileRepository.save(createFile("post/unused1.jpg", 0, null));
        fileRepository.save(createFile("post/unused2.jpg", 0, null));
        fileRepository.save(createFile("post/unused3.jpg", 0, null));

        fileRemover.removeUnusedFiles();

        assertThat(fileRepository.findAll()).isEmpty();
        BobTestConfiguration.TestFileStoragePort testPort = (BobTestConfiguration.TestFileStoragePort)fileStoragePort;
        assertThat(testPort.getDeletedFiles())
            .containsExactlyInAnyOrder("post/unused1.jpg", "post/unused2.jpg", "post/unused3.jpg");
    }
}
