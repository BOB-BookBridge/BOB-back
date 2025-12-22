package com.bob.core.member.adapter.api;

import static com.bob.core.shared.web.symbol.ResponseSymbol.CREATED;
import static com.bob.core.shared.web.symbol.ResponseSymbol.DELETED;

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

import com.bob.core.member.adapter.api.request.CreateMemberWishRequest;
import com.bob.core.member.adapter.api.response.MemberWishResponse;
import com.bob.core.member.application.dto.command.RegisterMemberWishCommand;
import com.bob.core.member.application.dto.command.RemoveMemberWishCommand;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberWishManager;
import com.bob.core.shared.web.AuthenticationId;
import com.bob.core.shared.web.CommonResponse;
import com.bob.core.shared.web.symbol.ResponseSymbol;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberWishApi {

    private final MemberReader memberReader;
    private final MemberWishManager wishManager;

    @PostMapping("/wishes")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerWish(
        @AuthenticationId UUID memberId,
        @Valid @RequestBody CreateMemberWishRequest request
    ) {
        RegisterMemberWishCommand command = RegisterMemberWishCommand.of(
            request.isbn(), request.title(), request.author(),
            request.description(), request.priceStandard(), request.cover(), request.pubDate()
        );

        wishManager.registerWish(memberId, command);

        return new CommonResponse<>(true, CREATED);
    }

    @GetMapping("/{memberId}/wishes")
    public ResponseEntity<List<MemberWishResponse>> readWishes(@PathVariable UUID memberId) {
        MemberDetail result = memberReader.readDetail(memberId, false);

        List<MemberWishResponse> wishes = result.wishes().stream()
            .map(MemberWishResponse::of)
            .toList();

        return ResponseEntity.ok(wishes);
    }

    @DeleteMapping("/wishes/{wishId}")
    public CommonResponse<ResponseSymbol> removeWish(@AuthenticationId UUID memberId, @PathVariable Long wishId) {
        RemoveMemberWishCommand command = new RemoveMemberWishCommand(wishId);

        wishManager.removeWish(memberId, command);

        return new CommonResponse<>(true, DELETED);
    }
}
