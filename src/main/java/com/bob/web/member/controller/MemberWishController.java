package com.bob.web.member.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.DELETED;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.dto.command.DeleteMemberWishCommand;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.usecase.MemberWishDeleteUseCase;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import com.bob.domain.member.usecase.MemberWishWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.member.request.CreateMemberWishRequest;
import com.bob.web.member.response.MemberWishesResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final MemberWishReadUseCase readUseCase;
  private final MemberWishDeleteUseCase deleteUseCase;

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

  @GetMapping("/{memberId}/wishes")
  public ResponseEntity<MemberWishesResponse> handleReadWishes(
      @PathVariable UUID memberId
  ) {
    ReadMemberWishesQuery query = ReadMemberWishesQuery.of(memberId);
    MemberWishesResult result = readUseCase.readWishesProcess(query);
    return ResponseEntity.ok(MemberWishesResponse.from(result));
  }

  @DeleteMapping("/wishes/{wishId}")
  public CommonResponse<ResponseSymbol> handleDeleteWish(
      @AuthenticationId UUID memberId,
      @PathVariable Long wishId
  ) {
    DeleteMemberWishCommand command = DeleteMemberWishCommand.of(memberId, wishId);
    deleteUseCase.deleteWishProcess(command);
    return new CommonResponse<>(true, DELETED);
  }
}
