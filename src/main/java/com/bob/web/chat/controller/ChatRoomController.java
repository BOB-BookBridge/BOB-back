package com.bob.web.chat.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.bob.domain.chat.service.dto.query.ReadChatRoomListQuery;
import com.bob.domain.chat.service.dto.response.ChatRoomSummaryResponse;
import com.bob.domain.chat.service.dto.response.CreateChatRoomResponse;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.domain.chat.usecase.ChatRoomWriteUseCase;
import com.bob.web.chat.request.CreateChatRoomRequest;
import com.bob.web.common.AuthenticationId;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatRoomController {

  private final ChatRoomWriteUseCase writeUseCase;
  private final ChatRoomReadUseCase readUseCase;

  @PostMapping
  public ResponseEntity<CreateChatRoomResponse> handleCreateChatRoom(
      @Valid @RequestBody CreateChatRoomRequest request,
      @AuthenticationId UUID memberId
  ) {
    CreateChatRoomResponse response = writeUseCase.createChatRoomProcess(request.toCommand(memberId));
    return ResponseEntity.status(CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<ChatRoomSummaryResponse>> handleReadChatRoomList(@AuthenticationId UUID memberId) {
    return ResponseEntity.ok().body(readUseCase.readChatRoomListProcess(ReadChatRoomListQuery.of(memberId)));
  }
}
