package com.bob.core.adapter.member.api;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.adapter.member.api.request.SendAuthenticationCodeRequest;
import com.bob.core.adapter.member.api.request.VerifyMailRequest;
import com.bob.core.application.member.dto.command.VerifyMailCommand;
import com.bob.core.application.member.port.in.MemberAuthenticator;

@RequiredArgsConstructor
@RequestMapping("/auth/email")
@RestController
public class MemberAuthApi {

    private final MemberAuthenticator authenticator;

    @PostMapping
    public CommonResponse<ResponseSymbol> sendAuthenticationCode(@RequestBody SendAuthenticationCodeRequest request) {
        authenticator.sendAuthenticationCode(request.email());

        return new CommonResponse<>(true, ResponseSymbol.SENT);
    }

    @PostMapping("/confirm")
    public CommonResponse<ResponseSymbol> verifyMail(@RequestBody VerifyMailRequest request) {
        VerifyMailCommand command = new VerifyMailCommand(request.code());

        authenticator.verifyMail(request.email(), command);

        return new CommonResponse<>(true, ResponseSymbol.VERIFIED);
    }
}
