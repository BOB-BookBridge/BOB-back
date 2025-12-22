package com.bob.core.area.application.port.in;

import com.bob.core.area.application.dto.command.MatchAreaQuery;

public interface AreaAuthenticator {

    void authenticate(Integer emdId, MatchAreaQuery command);
}
