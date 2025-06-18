package com.bob.domain.area.adapter;

import com.bob.domain.area.service.AreaService;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.post.service.dto.response.PostAreaSummaryResponse;
import com.bob.domain.post.service.port.PostAreaPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostAreaAdapter implements PostAreaPort {

  private final AreaService areaService;

  @Override
  public PostAreaSummaryResponse readPostAreaSummary(UUID memberId) {
    AreaSummaryResponse response = areaService.readAreaSummaryProcess(ReadAreaQuery.of(memberId));
    return PostAreaSummaryResponse.of(response.emdId(), response.emdName(), response.siggName(), response.validity());
  }
}
