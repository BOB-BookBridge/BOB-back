package com.bob.core.adapter.file.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.CREATED;
import static com.bob.core.adapter.common.symbol.ResponseSymbol.UPDATED;

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

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.adapter.file.api.request.GenerateFileUploadUrlRequest;
import com.bob.core.adapter.file.api.request.RegisterFileRequest;
import com.bob.core.adapter.file.api.request.UpdateFileRequest;
import com.bob.core.application.file.dto.command.GenerateFileUploadUrlCommand;
import com.bob.core.application.file.dto.command.RegisterFilesCommand;
import com.bob.core.application.file.dto.command.UpdateFilesCommand;
import com.bob.core.application.file.dto.result.FileUploadUrl;
import com.bob.core.application.file.port.in.FileModifier;
import com.bob.core.application.file.port.in.FileRegister;

@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileApi {

    private final FileRegister fileRegister;
    private final FileModifier fileModifier;

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
