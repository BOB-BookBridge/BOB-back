package com.bob.integration.adapter.member;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.area.application.dto.command.MatchAreaQuery;
import com.bob.core.area.application.port.in.AreaAuthenticator;
import com.bob.core.area.application.port.in.AreaReader;
import com.bob.core.area.domain.EmdArea;
import com.bob.core.area.domain.SidoArea;
import com.bob.core.area.domain.SiggArea;
import com.bob.core.member.application.port.out.MemberAreaPort;
import com.bob.core.member.application.port.result.MemberAreaResult;

@Component
@RequiredArgsConstructor
public class MemberAreaAdapter implements MemberAreaPort {

    private final AreaReader areaReader;
    private final AreaAuthenticator areaAuthenticator;

    @Override
    public MemberAreaResult read(Integer emdId) {
        EmdArea emdArea = areaReader.read(emdId);
        SiggArea siggArea = emdArea.getSiggArea();
        SidoArea sidoArea = siggArea.getSidoArea();
        return MemberAreaResult.of(emdArea.getId(), emdArea.getName(), siggArea.getName(), sidoArea.getName());
    }

    @Override
    public void authenticate(Integer emdId, Double lat, Double lon) {
        MatchAreaQuery query = MatchAreaQuery.of(lat, lon);
        areaAuthenticator.authenticate(emdId, query);
    }
}
