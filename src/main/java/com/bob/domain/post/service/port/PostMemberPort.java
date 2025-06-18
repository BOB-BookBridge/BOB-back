package com.bob.domain.post.service.port;

import com.bob.domain.post.service.dto.response.PostMemberSummaryResponse;
import java.util.UUID;

public interface PostMemberPort {

  PostMemberSummaryResponse readPostMemberSummary(UUID memberId);
}
