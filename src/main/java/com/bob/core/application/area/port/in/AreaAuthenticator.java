package com.bob.core.application.area.port.in;

import com.bob.core.application.area.dto.command.MatchAreaQuery;

public interface AreaAuthenticator {

    void authenticate(Integer emdId, MatchAreaQuery command);
}
