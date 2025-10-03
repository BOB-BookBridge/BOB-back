package com.bob.web.trade.controller;

import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.web.common.AuthenticationId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class PostTradeQueryController {

  private final TradeReadUseCase readUseCase;

  @GetMapping("/posts/{postId}/trades")
  public ResponseEntity<PostTradesResponse> handleReadTrades(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readPostTradesProcess(ReadPostTradesQuery.of(postId, memberId)));
  }
}
