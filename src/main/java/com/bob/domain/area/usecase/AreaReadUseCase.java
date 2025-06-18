package com.bob.domain.area.usecase;

import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;

public interface AreaReadUseCase {

  AreaSummaryResponse readAreaSummaryProcess(ReadAreaQuery query);
}
