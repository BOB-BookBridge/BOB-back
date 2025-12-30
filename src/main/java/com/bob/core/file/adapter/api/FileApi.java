package com.bob.core.file.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.CREATED;
import static com.bob.shared.web.response.ResponseSymbol.UPDATED;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.file.adapter.api.request.GenerateFileUploadUrlRequest;
import com.bob.core.file.adapter.api.request.RegisterFileRequest;
import com.bob.core.file.adapter.api.request.UpdateFileRequest;
import com.bob.core.file.application.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.file.application.dto.command.RegisterFilesCommand;
import com.bob.core.file.application.dto.command.UpdateFilesCommand;
import com.bob.core.file.application.dto.result.FileUploadUrl;
import com.bob.core.file.application.port.in.FileModifier;
import com.bob.core.file.application.port.in.FileRegister;
import com.bob.global.ratelimit.annotation.RateLimit;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileApi {

    private final FileRegister fileRegister;
    private final FileModifier fileModifier;

    @RateLimit(
        name = "register-files",
        windowSecond = 60, maxRequest = 10,
        target = RateLimit.LimitTarget.MEMBER_ID,
        value = "#memberId"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerFiles(
        @Valid @RequestBody RegisterFileRequest request,
        @AuthenticationId UUID memberId
    ) {
        RegisterFilesCommand command = new RegisterFilesCommand(request.domain(), request.fileNames(), memberId);

        fileRegister.registerFiles(command);

        return new CommonResponse<>(true, CREATED);
    }

    @RateLimit(
        name = "generate-upload-url",
        windowSecond = 60, maxRequest = 10
    )
    @PostMapping("/urls")
    public ResponseEntity<List<FileUploadUrl>> generateFileUploadUrl(
        @Valid @RequestBody GenerateFileUploadUrlRequest request
    ) {
        GenerateFileUploadUrlCommand command =
            new GenerateFileUploadUrlCommand(request.domain(), request.contentTypes());

        List<FileUploadUrl> fileUploadUrls = fileRegister.generateFileUploadUrl(command);

        return ResponseEntity.ok(fileUploadUrls);
    }

    @PutMapping
    public CommonResponse<ResponseSymbol> changeFile(
        @Valid @RequestBody UpdateFileRequest request,
        @AuthenticationId UUID memberId
    ) {
        UpdateFilesCommand command
            = new UpdateFilesCommand(request.domain(), request.referenceId(), request.fileNames(), memberId);

        fileModifier.updateFiles(command);

        return new CommonResponse<>(true, UPDATED);
    }
}
