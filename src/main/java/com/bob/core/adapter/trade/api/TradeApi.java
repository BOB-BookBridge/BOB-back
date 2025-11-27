package com.bob.core.adapter.trade.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.DELETED;
import static com.bob.core.adapter.common.symbol.ResponseSymbol.UPDATED;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.adapter.trade.api.request.ChangeTradeItemsRequest;
import com.bob.core.adapter.trade.api.request.ChangeTradeStatusRequest;
import com.bob.core.adapter.trade.api.request.CreateTradeRequest;
import com.bob.core.adapter.trade.api.request.ReadTradesRequest;
import com.bob.core.adapter.trade.api.response.ChangeTradeStatusResponse;
import com.bob.core.adapter.trade.api.response.CreateTradeResponse;
import com.bob.core.application.trade.dto.command.ChangeTradeItemsCommand;
import com.bob.core.application.trade.dto.command.ChangeTradeStatusCommand;
import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.application.trade.dto.command.RemoveTradeCommand;
import com.bob.core.application.trade.dto.query.ReadTradeDetailQuery;
import com.bob.core.application.trade.dto.result.ChangeTradeStatusResult;
import com.bob.core.application.trade.dto.result.TradeDetail;
import com.bob.core.application.trade.dto.result.TradeSummaries;
import com.bob.core.application.trade.port.in.TradeCreator;
import com.bob.core.application.trade.port.in.TradeModifier;
import com.bob.core.application.trade.port.in.TradeReader;
import com.bob.core.application.trade.port.in.TradeRemover;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.dsl.query.ReadTradesQuery;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trades")
public class TradeApi {

    private final TradeCreator tradeCreator;
    private final TradeReader tradeReader;
    private final TradeModifier tradeModifier;
    private final TradeRemover tradeRemover;

    @PostMapping
    public ResponseEntity<CreateTradeResponse> createTrade(
        @Valid @RequestBody CreateTradeRequest request,
        @AuthenticationId UUID memberId
    ) {
        CreateTradeCommand command =
            new CreateTradeCommand(request.postId(), memberId, request.itemIds(), request.isFar());

        Trade trade = tradeCreator.create(command);

        return ResponseEntity.status(CREATED).body(new CreateTradeResponse(trade.getId()));
    }

    @GetMapping
    public ResponseEntity<TradeSummaries> readTrades(
        @Valid ReadTradesRequest request,
        @AuthenticationId UUID memberId,
        Pageable pageable
    ) {
        ReadTradesQuery query = ReadTradesQuery.of(memberId, request.key(), request.status());

        return ResponseEntity.ok(tradeReader.readTrades(query, pageable));
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeDetail> readTradeDetail(
        @PathVariable Long tradeId,
        @AuthenticationId UUID memberId
    ) {
        ReadTradeDetailQuery query = new ReadTradeDetailQuery(memberId);

        return ResponseEntity.ok(tradeReader.readTradeDetail(tradeId, query));
    }

    @PatchMapping("/{tradeId}")
    public ResponseEntity<ChangeTradeStatusResponse> changeTradeStatus(
        @PathVariable Long tradeId,
        @Valid @RequestBody ChangeTradeStatusRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(memberId, request.status(), request.reason());

        ChangeTradeStatusResult result = tradeModifier.changeStatus(tradeId, command);

        return ResponseEntity.ok(new ChangeTradeStatusResponse(result.chatroomId()));
    }

    @PatchMapping("/{tradeId}/items")
    public CommonResponse<ResponseSymbol> changeTradeItem(
        @PathVariable Long tradeId,
        @Valid @RequestBody ChangeTradeItemsRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangeTradeItemsCommand command = new ChangeTradeItemsCommand(request.itemIds(), memberId);

        tradeModifier.changeItems(tradeId, command);

        return new CommonResponse<>(true, UPDATED);
    }

    @DeleteMapping("/{tradeId}")
    public CommonResponse<ResponseSymbol> removeTrade(
        @PathVariable Long tradeId,
        @AuthenticationId UUID memberId
    ) {
        RemoveTradeCommand command = new RemoveTradeCommand(memberId);

        tradeRemover.remove(tradeId, command);

        return new CommonResponse<>(true, DELETED);
    }
}
