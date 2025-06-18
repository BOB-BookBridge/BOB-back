package com.bob.domain.area.service.reader;

import com.bob.domain.area.entity.EmdArea;
import com.bob.domain.area.repository.EmdAreaRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EmdAreaReader {

  private final EmdAreaRepository emdAreaRepository;

  public EmdArea readEmdAreaById(Integer id) {
    return emdAreaRepository.findById(id)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_AREA));
  }
}
