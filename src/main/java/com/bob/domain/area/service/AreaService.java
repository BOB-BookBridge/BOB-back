package com.bob.domain.area.service;

import static com.bob.domain.area.entity.activity.ActivityArea.create;
import static com.bob.domain.area.entity.activity.ActivityArea.createId;
import static com.bob.global.utils.geo.GeometryUtils.createPoint;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.dto.command.CreateAreaCommand;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.area.service.reader.ActivityAreaReader;
import com.bob.domain.area.service.reader.EmdAreaReader;
import com.bob.domain.area.usecase.AreaModifyUseCase;
import com.bob.domain.area.usecase.AreaReadUseCase;
import com.bob.domain.area.usecase.AreaWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AreaService implements AreaWriteUseCase, AreaReadUseCase, AreaModifyUseCase {

  private final ActivityAreaRepository activityAreaRepository;
  private final ActivityAreaReader activityAreaReader;
  private final EmdAreaReader emdAreaReader;

  @Transactional
  public void createActivityAreaProcess(CreateAreaCommand command) {
    activityAreaRepository.save(command.toActivityArea());
  }

  @Transactional
  public void createNonAuthenticatedActivityAreaProcess(CreateAreaCommand command) {
    activityAreaRepository.save(command.toNonAuthenticatedActivityArea());
  }

  @Transactional
  public void authenticateProcess(AuthenticationCommand command) {
    verifyLocation(command);
    if (command.isGuest()) 
      return;
    createActivityArea(command);
  }

  private void createActivityArea(AuthenticationCommand command) {
    ActivityArea area = activityAreaReader.readActivityAreaByMemberId(command.memberId());
    activityAreaRepository.delete(area);
    ActivityAreaId newId = createId(command.memberId(), command.emdId());
    activityAreaRepository.save(create(newId));
  }

  private void verifyLocation(AuthenticationCommand command) {
    EmdArea emdArea = emdAreaReader.readEmdAreaById(command.emdId());
    Point point = createPoint(command.lat(), command.lon());
    if (!emdArea.getGeom().contains(point)) {
      throw new ApplicationException(ApplicationError.INVALID_AREA_AUTHENTICATION);
    }
  }

  @Transactional(readOnly = true)
  public AreaSummaryResponse readAreaSummaryProcess(ReadAreaQuery query) {
    ActivityArea activityArea = activityAreaReader.readActivityAreaByMemberId(query.memberId());
    EmdArea emdArea = emdAreaReader.readEmdAreaById(activityArea.getId().getEmdAreaId());
    return AreaSummaryResponse.of(
        activityArea.getId().getEmdAreaId(),
        emdArea.getName(),
        emdArea.getSiggArea().getName(),
        activityArea.isValidAuthentication(),
        activityArea.getAuthenticationAt()
    );
  }
}
