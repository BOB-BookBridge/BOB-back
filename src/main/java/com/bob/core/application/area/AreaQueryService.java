package com.bob.core.application.area;

import static com.bob.global.exception.response.ApplicationError.AREA_AUTHENTICATION_FAILED;
import static com.bob.global.utils.geo.GeometryUtils.createPoint;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.area.dto.command.MatchAreaQuery;
import com.bob.core.application.area.port.in.AreaAuthenticator;
import com.bob.core.application.area.port.in.AreaReader;
import com.bob.core.domain.area.EmdArea;
import com.bob.core.domain.area.repository.AreaRepository;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaQueryService implements AreaReader, AreaAuthenticator {

    private final AreaRepository areaRepository;

    @Override
    public EmdArea read(Integer emdAreaId) {
        return areaRepository.findById(emdAreaId)
            .orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다. id : " + emdAreaId));
    }

    @Override
    public void authenticate(Integer emdId, MatchAreaQuery query) {
        EmdArea emdArea = read(emdId);
        Point point = createPoint(query.lat(), query.lon());

        if (!emdArea.getGeom().contains(point))
            throw new ApplicationException(AREA_AUTHENTICATION_FAILED);
    }
}
