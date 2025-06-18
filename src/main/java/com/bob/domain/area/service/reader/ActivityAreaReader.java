package com.bob.domain.area.service.reader;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.repository.ActivityAreaRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ActivityAreaReader {

  private final ActivityAreaRepository activityAreaRepository;

  public ActivityArea readActivityAreaByMemberId(UUID memberId) {
    return activityAreaRepository.findByIdMemberId(memberId)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_ACTIVITY_AREA));
  }
}
