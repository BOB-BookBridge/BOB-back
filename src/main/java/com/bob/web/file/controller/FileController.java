package com.bob.web.file.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;

import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.file.usecase.FileWriteUseCase;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.file.request.ReadSingleFileUploadUrlRequest;
import com.bob.web.file.request.ReadMultiFileUploadUrlRequest;
import com.bob.web.file.request.RegisterFileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileController {

  private final FileWriteUseCase writeUseCase;
  private final FileReadUseCase readUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleRegisterFile(
      @Valid @RequestBody RegisterFileRequest request
  ) {
    writeUseCase.registerFileProcess(request.toCommand());
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping("/url")
  public ResponseEntity<ReadSingleFileUploadUrlResponse> handleReadSingleFileUploadUrl(
      @Valid @RequestBody ReadSingleFileUploadUrlRequest request
  ) {
    return ResponseEntity.ok(readUseCase.readSingleFileUploadUrl(request.toQuery()));
  }

  @GetMapping("/urls")
  public ResponseEntity<ReadMultiFileUploadUrlResponse> handleReadMultiFileUploadUrl(
      @Valid @RequestBody ReadMultiFileUploadUrlRequest request
  ) {
    return ResponseEntity.ok(readUseCase.readMultiFileUploadUrl(request.toQuery()));
  }
}
