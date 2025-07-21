package com.bob.web.trade.controller;

import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;

import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.trade.request.ChangeTradeStatusRequest;
import com.bob.web.trade.request.ReadFilteredTradesRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trades")
public class TradeController {

  private final TradeReadUseCase readUseCase;
  private final TradeModifyUseCase modifyUseCase;

  @GetMapping
  public ResponseEntity<TradesResponse> handleReadTrades(
      ReadFilteredTradesRequest request,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readTradesProcess(request.toQuery(memberId)));
  }

  @PatchMapping("/{tradeId}")
  public CommonResponse<ResponseSymbol> handleModifyTradeStatus(
      @PathVariable Long tradeId,
      @Valid @RequestBody ChangeTradeStatusRequest request,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changeTradeStatusProcess(request.toCommand(memberId, tradeId));
    return new CommonResponse<>(true, UPDATED);
  }
}
