package com.bob.core.adapter.bookcase.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.CREATED;
import static com.bob.core.adapter.common.symbol.ResponseSymbol.DELETED;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

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

import com.bob.core.adapter.bookcase.api.request.ReadBookcasesRequest;
import com.bob.core.adapter.bookcase.api.request.RegisterBookcaseRequest;
import com.bob.core.adapter.bookcase.api.response.BookcaseItemDetailResponse;
import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.application.bookcase.dto.command.DeleteBookcaseItemCommand;
import com.bob.core.application.bookcase.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.application.bookcase.dto.query.ReadBookcaseQuery;
import com.bob.core.application.bookcase.dto.result.BookcaseItemDetail;
import com.bob.core.application.bookcase.port.in.BookcaseDeleter;
import com.bob.core.application.bookcase.port.in.BookcaseReader;
import com.bob.core.application.bookcase.port.in.BookcaseRegister;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class BookcaseApi {

    private final BookcaseRegister bookcaseRegister;
    private final BookcaseReader bookcaseReader;
    private final BookcaseDeleter bookcaseDeleter;

    @PostMapping("/bookcase")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerBookcaseItem(
        @AuthenticationId UUID memberId,
        @Valid @RequestBody RegisterBookcaseRequest request
    ) {
        RegisterBookcaseItemCommand command = RegisterBookcaseItemCommand.of(
            memberId, request.status(), request.isbn(), request.title(), request.author(),
            request.description(), request.priceStandard(), request.cover(), request.pubDate()
        );

        bookcaseRegister.registerItem(command);

        return new CommonResponse<>(true, CREATED);
    }

    @GetMapping("/{memberId}/bookcase")
    public ResponseEntity<List<BookcaseItemDetailResponse>> readBookcase(
        @PathVariable UUID memberId,
        ReadBookcasesRequest request
    ) {
        List<Long> requires = request.require() == null ? List.of(-1L) : request.require();

        ReadBookcaseQuery query = ReadBookcaseQuery.of(memberId, request.key(), requires);

        List<BookcaseItemDetail> result = bookcaseReader.readItemDetailsByQuery(query);

        return ResponseEntity.ok(BookcaseItemDetailResponse.listOf(result));
    }

    @DeleteMapping("/bookcase/{itemId}")
    public CommonResponse<ResponseSymbol> deleteBookcaseItem(
        @AuthenticationId UUID memberId,
        @PathVariable Long itemId
    ) {
        DeleteBookcaseItemCommand command = DeleteBookcaseItemCommand.of(memberId);

        bookcaseDeleter.delete(itemId, command);

        return new CommonResponse<>(true, DELETED);
    }
}
