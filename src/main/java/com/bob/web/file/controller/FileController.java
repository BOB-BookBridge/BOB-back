package com.bob.web.file.controller;

import com.bob.domain.file.service.dto.response.ReadMultiFileUploadUrlResponse;
import com.bob.domain.file.service.dto.response.ReadSingleFileUploadUrlResponse;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.web.file.request.ReadSingleFileUploadUrlRequest;
import com.bob.web.file.request.ReadMultiFileUploadUrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileController {

  private final FileReadUseCase readUseCase;

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
