package com.bob.core.application.file.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bob.core.application.file.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.application.file.dto.command.RegisterFilesCommand;
import com.bob.core.application.file.dto.result.FileUploadUrl;
import com.bob.core.domain.file.File;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.global.utils.image.ImageDirectory;
import com.bob.global.utils.image.ImageUtils;
import com.bob.support.annotation.ContainerTest;

@DisplayName("파일 등록 테스트")
@ContainerTest
record FileRegisterTest(FileRegister fileRegister, FileRepository fileRepository) {

    @Test
    void 파일_등록() {
        RegisterFilesCommand command = new RegisterFilesCommand("POST", List.of("post/1.jpg", "post/2.jpg"), MEMBER_ID);

        List<File> result = fileRegister.registerFiles(command);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(File::getName).containsExactly("post/1.jpg", "post/2.jpg");
        assertThat(result).extracting(File::getUploader).containsOnly(MEMBER_ID);
    }

    @Test
    void 업로드_URL_발급() {
        List<String> contentTypes = List.of("image/png", "image/jpeg");
        GenerateFileUploadUrlCommand command = new GenerateFileUploadUrlCommand("post", contentTypes);

        List<String> fileNames = List.of("post/uuid1.png", "post/uuid2.jpg");

        try (MockedStatic<ImageUtils> utils = org.mockito.Mockito.mockStatic(ImageUtils.class)) {
            utils.when(() -> ImageUtils.generateImageFileNames(ImageDirectory.POST, contentTypes))
                .thenReturn(fileNames);

            List<FileUploadUrl> result = fileRegister.generateFileUploadUrl(command);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(FileUploadUrl::fileName).containsExactly("post/uuid1.png", "post/uuid2.jpg");
            assertThat(result).extracting(FileUploadUrl::uploadUrl)
                .containsExactly("https://mock-url.com/post/uuid1.png", "https://mock-url.com/post/uuid2.jpg");
            assertThat(result).extracting(FileUploadUrl::sequence).containsExactly(0, 1);
        }
    }
}
