package com.bob.web.trade.controller;

import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;
import static org.springframework.http.HttpStatus.CREATED;

import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.trade.request.ChangeTradeItemsRequest;
import com.bob.web.trade.request.ChangeTradeStatusRequest;
import com.bob.web.trade.request.CreateTradeRequest;
import com.bob.web.trade.request.ReadTradesRequest;
import com.bob.web.trade.response.ChangeTradeStatusResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trades")
public class TradeController {

  private final TradeWriteUseCase writeUseCase;
  private final TradeReadUseCase readUseCase;
  private final TradeModifyUseCase modifyUseCase;

  @PostMapping
  public ResponseEntity<CreateTradeResponse> handleCreateTrade(
      @Valid @RequestBody CreateTradeRequest request,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.status(CREATED).body(writeUseCase.createTradeProcess(request.toCommand(memberId)));
  }

  @GetMapping
  public ResponseEntity<TradesResponse> handleReadTrades(
      @Valid ReadTradesRequest request,
      @AuthenticationId UUID memberId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(readUseCase.readTradesProcess(ReadTradesQuery.of(memberId, request.key(), request.status()), pageable));
  }

  @GetMapping("/{tradeId}")
  public ResponseEntity<TradeDetailResponse> handleReadTradeDetail(
      @PathVariable Long tradeId,
      @AuthenticationId UUID memberId
  ) {
    ReadTradeDetailQuery query = ReadTradeDetailQuery.of(tradeId, memberId);
    return ResponseEntity.ok(readUseCase.readTradeDetailProcess(query));
  }

  @PatchMapping("/{tradeId}")
  public ResponseEntity<ChangeTradeStatusResponse> handleModifyTradeStatus(
      @PathVariable Long tradeId,
      @Valid @RequestBody ChangeTradeStatusRequest request,
      @AuthenticationId UUID memberId
  ) {
    ChangeTradeStatusResult result = modifyUseCase.changeTradeStatusProcess(request.toCommand(memberId, tradeId));
    return ResponseEntity.ok(ChangeTradeStatusResponse.of(result.chatroomId()));
  }

  @PatchMapping("/{tradeId}/items")
  public CommonResponse<ResponseSymbol> handleModifyTradeItem(
      @PathVariable Long tradeId,
      @Valid @RequestBody ChangeTradeItemsRequest request,
      @AuthenticationId UUID memberId
  ) {
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, request.itemIds(), memberId);
    modifyUseCase.changeTradeItemProcess(command);
    return new CommonResponse<>(true, UPDATED);
  }
}
