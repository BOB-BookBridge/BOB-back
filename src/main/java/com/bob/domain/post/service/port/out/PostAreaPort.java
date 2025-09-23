package com.bob.domain.post.service.port.out;

import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import java.util.UUID;

public interface PostAreaPort {

  AreaSummaryResponse readPostAreaSummary(UUID memberId);
}
