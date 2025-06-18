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
import java.time.LocalDate;
import java.util.Objects;
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
    ActivityArea activityArea = command.toActivityArea();
    activityAreaRepository.save(activityArea);
  }

  @Transactional
  public void authenticateProcess(AuthenticationCommand command) {
    validateLocation(command);
    if (command.isSignup()) {
      return;
    }
    verifyLoginMember(command.isGuest());
    switch (command.purpose()) {
      case CHANGE_AREA -> handleChangeArea(command);
      case RE_AUTHENTICATE -> handleReAuthenticate(command);
    }
  }

  private void handleChangeArea(AuthenticationCommand command) {
    ActivityArea area = activityAreaReader.readActivityAreaByMemberId(command.memberId());
    verifyIsSameArea(area.getId().getEmdAreaId(), command.emdId());
    activityAreaRepository.delete(area);

    ActivityAreaId newId = createId(command.memberId(), command.emdId());
    activityAreaRepository.save(create(newId));
  }

  private void handleReAuthenticate(AuthenticationCommand command) {
    ActivityArea area = activityAreaReader.readActivityAreaByMemberId(command.memberId());
    area.updateAuthenticationAt(LocalDate.now());
  }

  private void validateLocation(AuthenticationCommand command) {
    EmdArea emdArea = emdAreaReader.readEmdAreaById(command.emdId());
    Point point = createPoint(command.lat(), command.lon());
    if (!emdArea.getGeom().contains(point)) {
      throw new ApplicationException(ApplicationError.INVALID_AREA_AUTHENTICATION);
    }
  }

  private void verifyLoginMember(boolean isGuest) {
    if (isGuest) {
      throw new ApplicationException(ApplicationError.NOT_EXISTS_MEMBER);
    }
  }

  private void verifyIsSameArea(Integer oldId, Integer newId) {
    if (Objects.equals(oldId, newId)) {
      throw new ApplicationException(ApplicationError.IS_SAME_REQUEST);
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
