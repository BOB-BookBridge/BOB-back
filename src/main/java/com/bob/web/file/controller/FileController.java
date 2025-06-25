package com.bob.web.file.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;

import com.bob.domain.file.service.dto.response.FileUploadUrlResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.file.usecase.FileWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.file.request.ChangeFileRequest;
import com.bob.web.file.request.ReadFileUploadUrlRequest;
import com.bob.web.file.request.RegisterFileRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  private final FileModifyUseCase modifyUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleRegisterFile(
      @Valid @RequestBody RegisterFileRequest request,
      @AuthenticationId UUID memberId
  ) {
    writeUseCase.registerFileProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping("/url")
  public ResponseEntity<FileUploadUrlResponse> handleReadFileUploadUrl(
      @Valid @RequestBody ReadFileUploadUrlRequest request
  ) {
    return ResponseEntity.ok(readUseCase.readFileUploadUrl(request.toQuery()));
  }

  @PutMapping
  public CommonResponse<ResponseSymbol> handleChangeFile(
      @Valid @RequestBody ChangeFileRequest request,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changeFileProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, UPDATED);
  }
}
