package com.bob.core.adapter.area.api;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.area.api.request.AuthenticateAreaRequest;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.application.area.dto.command.MatchAreaQuery;
import com.bob.core.application.area.port.in.AreaAuthenticator;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaApi {

    private final AreaAuthenticator areaAuthenticator;

    @PostMapping("/authentication")
    public CommonResponse<ResponseSymbol> authenticate(@RequestBody AuthenticateAreaRequest request) {
        MatchAreaQuery query = MatchAreaQuery.of(request.lat(), request.lon());
        areaAuthenticator.authenticate(request.emdId(), query);

        return new CommonResponse<>(true, ResponseSymbol.OK);
    }
}
