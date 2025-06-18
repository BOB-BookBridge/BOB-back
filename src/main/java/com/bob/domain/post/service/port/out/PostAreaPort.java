package com.bob.domain.post.service.port.out;

import com.bob.domain.post.service.dto.response.PostAreaSummaryResponse;
import java.util.UUID;

public interface PostAreaPort {

  PostAreaSummaryResponse readPostAreaSummary(UUID memberId);
}
