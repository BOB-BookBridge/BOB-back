package com.bob.core.area.adapter.api;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.area.adapter.api.request.AuthenticateAreaRequest;
import com.bob.core.area.application.dto.command.MatchAreaQuery;
import com.bob.core.area.application.port.in.AreaAuthenticator;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

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
