package com.bob.web.member.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.DELETED;

import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.member.request.ReadMemberBooksRequest;
import com.bob.web.member.request.RegisterMemberBookRequest;
import jakarta.validation.Valid;
import java.util.List;
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
public class MemberBookController {

  private final MemberBookWriteUseCase writeUseCase;
  private final MemberBookReadUseCase readUseCase;
  private final MemberBookRemoveUseCase removeUseCase;

  @PostMapping("/books")
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleCreateMemberBook(
      @AuthenticationId UUID memberId,
      @Valid @RequestBody RegisterMemberBookRequest request
  ) {
    writeUseCase.registerMemberBookProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping("/{memberId}/books")
  public ResponseEntity<MemberBooksResponse> handleReadBooks(
      ReadMemberBooksRequest request,
      @PathVariable UUID memberId
  ) {
    List<Long> requires = request.require() == null ? List.of(-1L) : request.require();
    ReadMemberBooksQuery query = ReadMemberBooksQuery.of(memberId, request.key(), requires);
    return ResponseEntity.ok(readUseCase.readMemberBooksProcess(query));
  }

  @DeleteMapping("/books/{memberBookId}")
  public CommonResponse<ResponseSymbol> handleRemoveMemberBook(
      @AuthenticationId UUID memberId,
      @PathVariable Long memberBookId
  ) {
    removeUseCase.removeMemberBookProcess(RemoveMemberBookCommand.of(memberId, memberBookId));
    return new CommonResponse<>(true, DELETED);
  }
}
