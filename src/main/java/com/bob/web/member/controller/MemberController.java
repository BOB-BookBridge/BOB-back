package com.bob.web.member.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.SENT;
import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;
import static com.bob.web.member.request.ReadProfileRequest.toQuery;

import com.bob.domain.member.service.dto.response.MemberProfileImageUrlResponse;
import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import com.bob.domain.member.usecase.MemberModifyUseCase;
import com.bob.domain.member.usecase.MemberReadUseCase;
import com.bob.domain.member.usecase.MemberWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.member.request.ChangePasswordRequest;
import com.bob.web.member.request.ChangeProfileRequest;
import com.bob.web.member.request.IssuePasswordRequest;
import com.bob.web.member.request.ReadImageUploadUrlRequest;
import com.bob.web.member.request.SignupRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

  private final MemberWriteUseCase writeUseCase;
  private final MemberReadUseCase readUseCase;
  private final MemberModifyUseCase modifyUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleSignup(@Valid @RequestBody SignupRequest request) {
    writeUseCase.signupProcess(request.toCommand());
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping("/me")
  public ResponseEntity<MemberProfileResponse> handleReadProfile(@AuthenticationId UUID memberId) {
    return ResponseEntity.ok(readUseCase.readProfileProcess(toQuery(memberId)));
  }

  @GetMapping("/{memberId}")
  public ResponseEntity<MemberProfileResponse> handleReadProfileById(@PathVariable UUID memberId) {
    return ResponseEntity.ok(readUseCase.readProfileProcess(toQuery(memberId)));
  }

  @PatchMapping("/me")
  public CommonResponse<ResponseSymbol> handleChangeProfile(
      @Valid @RequestBody ChangeProfileRequest request,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changeProfileProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, UPDATED);
  }

  @PatchMapping("/me/password")
  public CommonResponse<ResponseSymbol> handleChangePassword(
      @Valid @RequestBody ChangePasswordRequest request,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changePasswordProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, UPDATED);
  }

  @PatchMapping("/temp/password")
  public CommonResponse<ResponseSymbol> handleSendTempPassword(@Valid @RequestBody IssuePasswordRequest request) {
    modifyUseCase.issueTempPasswordProcess(request.toCommand());
    return new CommonResponse<>(true, SENT);
  }

  @PatchMapping("/me/image")
  public ResponseEntity<MemberProfileImageUrlResponse> handleGetImageUploadUrl(
      @Valid @RequestBody ReadImageUploadUrlRequest request,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(modifyUseCase.changeProfileImageUrlProcess(request.toQuery(memberId)));
  }
}
