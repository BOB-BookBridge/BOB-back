package com.bob.core.member.adapter.api;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.member.adapter.api.request.SendAuthenticationCodeRequest;
import com.bob.core.member.adapter.api.request.VerifyMailRequest;
import com.bob.core.member.application.dto.command.VerifyMailCommand;
import com.bob.core.member.application.port.in.MemberAuthenticator;
import com.bob.core.shared.web.CommonResponse;
import com.bob.core.shared.web.symbol.ResponseSymbol;
import com.bob.global.ratelimit.annotation.RateLimit;

@RequiredArgsConstructor
@RequestMapping("/auth/email")
@RestController
public class MemberAuthApi {

    private final MemberAuthenticator authenticator;

    @RateLimit(
        name = "send-auth-code",
        windowSecond = 180, maxRequest = 5
    )
    @PostMapping
    public CommonResponse<ResponseSymbol> sendAuthenticationCode(@RequestBody SendAuthenticationCodeRequest request) {
        authenticator.sendAuthenticationCode(request.email());

        return new CommonResponse<>(true, ResponseSymbol.SENT);
    }

    @RateLimit(
        name = "verify-email",
        windowSecond = 180, maxRequest = 5
    )
    @PostMapping("/confirm")
    public CommonResponse<ResponseSymbol> verifyMail(@RequestBody VerifyMailRequest request) {
        VerifyMailCommand command = new VerifyMailCommand(request.code());

        authenticator.verifyMail(request.email(), command);

        return new CommonResponse<>(true, ResponseSymbol.VERIFIED);
    }
}
