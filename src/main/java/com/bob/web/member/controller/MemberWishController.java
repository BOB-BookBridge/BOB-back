package com.bob.web.member.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.usecase.MemberWishWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.member.request.CreateMemberWishRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberWishController {

  private final MemberWishWriteUseCase writeUseCase;

  @PostMapping("/wishes")
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleCreateWish(
      @AuthenticationId UUID memberId,
      @Valid @RequestBody CreateMemberWishRequest request
  ) {
    CreateMemberWishCommand command = CreateMemberWishCommand.of(memberId, request.isbn(), request.title(),
        request.author(), request.description(), request.priceStandard(), request.cover(), request.pubDate());
    writeUseCase.createMemberWishProcess(command);
    return new CommonResponse<>(true, CREATED);
  }
}
