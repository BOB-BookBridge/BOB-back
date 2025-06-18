package com.bob.web.area.controller;

import com.bob.domain.area.service.AreaService;
import com.bob.web.area.request.AuthenticationRequest;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/areas")
@RestController
public class AreaController {

  private final AreaService areaAuthenticationService;

  @PatchMapping("/authentication")
  public CommonResponse<ResponseSymbol> handleAreaAuthentication(
      @RequestBody AuthenticationRequest request,
      @AuthenticationId UUID memberId
  ) {
    areaAuthenticationService.authenticateProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, ResponseSymbol.OK);
  }
}
