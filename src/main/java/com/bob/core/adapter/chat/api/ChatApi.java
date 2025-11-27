package com.bob.core.adapter.chat.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.UPDATED;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

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

import com.bob.core.adapter.chat.api.request.CreateChatMessageRequest;
import com.bob.core.adapter.chat.api.response.ChatMessageSendResponse;
import com.bob.core.adapter.chat.api.response.ChatroomDetailResponse;
import com.bob.core.adapter.chat.api.response.UnreadMessageCountResponse;
import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.application.chat.dto.command.CreateMessageCommand;
import com.bob.core.application.chat.dto.command.ExitChatroomCommand;
import com.bob.core.application.chat.dto.query.ReadChatMessagesQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomDetailQuery;
import com.bob.core.application.chat.dto.query.ReadChatroomSummariesQuery;
import com.bob.core.application.chat.dto.query.ReadUnreadMessageCountQuery;
import com.bob.core.application.chat.dto.result.ChatMessageCreationResult;
import com.bob.core.application.chat.dto.result.ChatMessageSummary;
import com.bob.core.application.chat.dto.result.ChatroomDetail;
import com.bob.core.application.chat.dto.result.ChatroomSummary;
import com.bob.core.application.chat.port.in.ChatMessageCreator;
import com.bob.core.application.chat.port.in.ChatMessageReader;
import com.bob.core.application.chat.port.in.ChatroomModifier;
import com.bob.core.application.chat.port.in.ChatroomReader;
import com.bob.core.application.trade.port.in.TradeReader;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatApi {

    private final ChatroomReader chatroomReader;
    private final ChatroomModifier chatroomModifier;

    private final ChatMessageCreator messageCreator;
    private final ChatMessageReader messageReader;

    private final TradeReader tradeReader;

    @PostMapping("/{chatroomId}/messages")
    @ResponseStatus(CREATED)
    public ResponseEntity<ChatMessageSendResponse> sendChatMessage(
        @Valid @RequestBody CreateChatMessageRequest request,
        @AuthenticationId UUID memberId,
        @PathVariable Long chatroomId
    ) {
        CreateMessageCommand command = new CreateMessageCommand(memberId, request.message(), request.fileNames());

        ChatMessageCreationResult result = messageCreator.createChatMessage(chatroomId, command);

        return ResponseEntity.status(CREATED)
            .body(ChatMessageSendResponse.of(result.message(), result.partnerLastReadMessageId()));
    }

    @GetMapping
    public ResponseEntity<List<ChatroomSummary>> readChatroomSummaries(@AuthenticationId UUID memberId) {
        ReadChatroomSummariesQuery query = new ReadChatroomSummariesQuery(memberId);

        return ResponseEntity.ok().body(chatroomReader.readChatRoomSummaries(query));
    }

    @GetMapping("/messages/unread")
    public ResponseEntity<UnreadMessageCountResponse> readUnreadMessageCount(@AuthenticationId UUID memberId) {
        int count = messageReader.countUnreadMessagesByMember(new ReadUnreadMessageCountQuery(memberId));

        return ResponseEntity.ok(new UnreadMessageCountResponse(count));
    }

    @GetMapping("/{chatroomId}")
    public ResponseEntity<ChatroomDetailResponse> readChatroomDetail(
        @PathVariable Long chatroomId,
        @AuthenticationId UUID memberId
    ) {
        ReadChatroomDetailQuery query = new ReadChatroomDetailQuery(memberId);

        ChatroomDetail result = chatroomReader.readChatRoomDetail(chatroomId, query);

        String status = tradeReader.readTradeStatus(result.tradeId());

        return ResponseEntity.ok().body(ChatroomDetailResponse.of(result, status));
    }

    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<List<ChatMessageSummary>> readChatMessageSummaries(
        @PathVariable Long chatroomId,
        @AuthenticationId UUID memberId
    ) {
        ReadChatMessagesQuery query = new ReadChatMessagesQuery(memberId);

        return ResponseEntity.ok(messageReader.readMessages(chatroomId, query));
    }

    @PatchMapping("/{chatroomId}")
    public CommonResponse<ResponseSymbol> exitChatroom(@PathVariable Long chatroomId, @AuthenticationId UUID memberId) {
        ExitChatroomCommand command = new ExitChatroomCommand(memberId);

        chatroomModifier.exit(chatroomId, command);

        return new CommonResponse<>(true, UPDATED);
    }
}
