package com.bob.web.area.adapter.in;

import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.area.usecase.AreaReadUseCase;
import com.bob.domain.post.service.port.out.PostAreaPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostAreaAdapter implements PostAreaPort {

  private final AreaReadUseCase readUseCase;

  @Override
  public AreaSummaryResponse readPostAreaSummary(UUID memberId) {
     return readUseCase.readAreaSummaryProcess(ReadAreaQuery.of(memberId));
  }
}
