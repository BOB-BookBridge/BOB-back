package com.bob.infrastructure.storage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.infrastructure.storage.repository.FileRepository;

@DisplayName("파일 저장소 테스트")
@ExtendWith(MockitoExtension.class)
class FileStorageAdapterTest {

    @InjectMocks
    private FileStorageAdapter fileStorageAdapter;

    @Mock
    private FileRepository fileRepository;

    @Test
    void 파일_업로드_URL_생성() {
        List<String> fileNames = List.of("profile/user1.png", "profile/user2.jpg");
        List<String> contentTypes = List.of("image/png", "image/jpeg");
        List<String> expectedUrls = List.of(
            "https://s3.amazonaws.com/bucket/profile/user1.png?X-Amz-Algorithm=...",
            "https://s3.amazonaws.com/bucket/profile/user2.jpg?X-Amz-Algorithm=..."
        );
        given(fileRepository.generateUploadUrls(fileNames, contentTypes)).willReturn(expectedUrls);

        List<String> uploadUrls = fileStorageAdapter.generateUploadUrls(fileNames, contentTypes);

        assertThat(uploadUrls).hasSize(2).isEqualTo(expectedUrls);

        then(fileRepository).should().generateUploadUrls(fileNames, contentTypes);
    }

    @Test
    void 파일_삭제() {
        List<String> fileNames = List.of("uploads/test-file1.txt", "uploads/test-file2.jpg");

        fileStorageAdapter.deleteFiles(fileNames);

        then(fileRepository).should().deleteFiles(fileNames);
    }
}
