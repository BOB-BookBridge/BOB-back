package com.bob.infra.auth.adapter;

import com.bob.infra.auth.service.TokenService;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class TokenAuthAdapter {

  private final TokenService tokenService;

  @PostMapping("/token/refresh")
  public CommonResponse<ResponseSymbol> handleReIssueToken(HttpServletRequest request, HttpServletResponse response) {
    tokenService.reIssueTokenProcess(request, response);
    return new CommonResponse<>(true, ResponseSymbol.OK);
  }
}
