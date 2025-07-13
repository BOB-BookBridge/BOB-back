package com.bob.web.chat.controller;

import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;
import static org.springframework.http.HttpStatus.CREATED;

import com.bob.domain.chat.service.dto.command.ExitChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ReadChatRoomDetailQuery;
import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.response.ChatRoomDetailResponse;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.usecase.ChatRoomModifyUseCase;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import com.bob.web.chat.request.CreateChatMessageRequest;
import com.bob.web.chat.request.CreateChatRoomRequest;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/chatrooms")
@RestController
public class ChatRoomController {

  private final ChatRoomWriteUseCase writeUseCase;
  private final ChatRoomReadUseCase readUseCase;

  private final ChatRoomModifyUseCase memberModifyUseCase;

  @PostMapping
  public ResponseEntity<CreateChatRoomResponse> handleCreateChatRoom(
      @Valid @RequestBody CreateChatRoomRequest request,
      @AuthenticationId UUID memberId
  ) {
    CreateChatRoomResponse response = writeUseCase.createChatRoomProcess(request.toCommand(memberId));
    return ResponseEntity.status(CREATED).body(response);
  }

  @PostMapping("/{chatroomId}/messages")
  @ResponseStatus(CREATED)
  public CommonResponse<ResponseSymbol> handleSendMessage(
      @Valid @RequestBody CreateChatMessageRequest request,
      @AuthenticationId UUID memberId,
      @PathVariable Long chatroomId
  ) {
    writeUseCase.createChatRoomMessageProcess(request.toCommand(chatroomId, memberId));
    return new CommonResponse<>(true, ResponseSymbol.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<ChatRoomSummaryResponse>> handleReadChatRoomList(@AuthenticationId UUID memberId) {
    return ResponseEntity.ok().body(readUseCase.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId)));
  }

  @GetMapping("/{chatroomId}")
  public ResponseEntity<ChatRoomDetailResponse> handleReadChatRoomDetail(
      @PathVariable Long chatroomId,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok().body(readUseCase.readChatRoomDetailProcess(ReadChatRoomDetailQuery.of(chatroomId, memberId)));
  }

  @PatchMapping("/{chatroomId}")
  public CommonResponse<ResponseSymbol> handleExitChatRoom(
      @PathVariable Long chatroomId,
      @AuthenticationId UUID memberId
  ) {
    memberModifyUseCase.exitChatRoomProcess(ExitChatRoomCommand.of(chatroomId, memberId));
    return new CommonResponse<>(true, UPDATED);
  }
}
