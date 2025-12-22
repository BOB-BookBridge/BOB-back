package com.bob.core.file.adapter.api;

import static com.bob.support.fixture.file.domain.FileFixture.createFile;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.file.adapter.api.request.GenerateFileUploadUrlRequest;
import com.bob.core.file.adapter.api.request.RegisterFileRequest;
import com.bob.core.file.adapter.api.request.UpdateFileRequest;
import com.bob.core.file.application.dto.result.FileUploadUrl;
import com.bob.core.file.domain.File;
import com.bob.core.file.domain.repository.FileRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("파일 API 테스트")
@BobApiTest
record FileApiTest(MockMvcTester mvcTester, FileRepository fileRepository, ObjectMapper objectMapper) {

    @Test
    void 파일_등록() throws Exception {
        setAuthentication();

        var request = new RegisterFileRequest("POST", List.of("post/1.jpg", "post/2.jpg"));
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatus(201);

        List<File> files = (List<File>)fileRepository.findAll();
        assertThat(files).hasSize(2);
        assertThat(files).extracting(File::getName).containsExactlyInAnyOrder("post/1.jpg", "post/2.jpg");
    }

    @Test
    void 업로드_URL_발급() throws Exception {
        var request = new GenerateFileUploadUrlRequest("post", List.of("image/png", "image/jpeg"));
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/files/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        List<FileUploadUrl> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<List<FileUploadUrl>>() {
            }
        );

        assertThat(response).hasSize(2);
        assertThat(response).allMatch(url -> url.uploadUrl().startsWith("https://mock-url.com/"));
        assertThat(response).extracting(FileUploadUrl::sequence).containsExactly(0, 1);
    }

    @Test
    void 파일_수정() throws Exception {
        String referenceId = "1";
        File file1 = createFile("post/old1.jpg", 0, referenceId);
        File file2 = createFile("post/old2.jpg", 1, referenceId);
        fileRepository.save(file1);
        fileRepository.save(file2);

        setAuthentication();

        var request = new UpdateFileRequest("POST", referenceId, List.of("post/new1.jpg", "post/new2.jpg"));
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.put()
            .uri("/files")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        List<File> allFiles = (List<File>)fileRepository.findAll();
        assertThat(allFiles).hasSize(2);
        assertThat(allFiles).extracting(File::getName).containsExactlyInAnyOrder("post/new1.jpg", "post/new2.jpg");
        assertThat(allFiles).allMatch(file -> referenceId.equals(file.getReferenceId()));
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MEMBER_ID, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
