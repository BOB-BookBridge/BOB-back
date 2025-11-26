package com.bob.core.adapter.trade.api;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.application.trade.dto.query.ReadPostTradesQuery;
import com.bob.core.application.trade.dto.result.internal.PostTrade;
import com.bob.core.application.trade.port.in.TradeReader;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class PostTradeQueryApi {

    private final TradeReader tradeReader;

    @GetMapping("/posts/{postId}/trades")
    public ResponseEntity<List<PostTrade>> readPostTrades(
        @PathVariable Long postId,
        @AuthenticationId UUID memberId
    ) {
        ReadPostTradesQuery query = new ReadPostTradesQuery(postId, memberId);

        return ResponseEntity.ok(tradeReader.readPostTradeSummaries(query));
    }
}
