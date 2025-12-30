package com.bob.security.adapter.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.security.application.port.in.TokenIssuer;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenApi {

    private final TokenIssuer tokenIssuer;

    @PostMapping("/token/refresh")
    public CommonResponse<ResponseSymbol> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        tokenIssuer.reissue(request, response);
        return new CommonResponse<>(true, ResponseSymbol.OK);
    }
}
