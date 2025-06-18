package com.bob.domain.member.service.port;

import com.bob.domain.post.service.dto.response.PostFavoritesResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MemberPostPort {

  PostsResponse readMemberPostSummary(UUID memberId, Pageable pageable);

  PostFavoritesResponse readMemberFavoritePostsSummary(UUID memberId, Pageable pageable);
}
