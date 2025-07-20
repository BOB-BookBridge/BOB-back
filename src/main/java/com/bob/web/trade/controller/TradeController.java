package com.bob.web.trade.controller;

import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.web.common.AuthenticationId;
import com.bob.web.trade.request.ReadFilteredTradesRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trades")
public class TradeController {

  private final TradeReadUseCase readUseCase;

  @GetMapping
  public ResponseEntity<TradesResponse> handleReadTrades(
      ReadFilteredTradesRequest request,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readTradesProcess(request.toQuery(memberId)));
  }
}
