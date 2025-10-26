package com.bob.domain.post.service.port.out;

import com.bob.domain.post.service.port.view.PostMemberWishesView;
import java.util.UUID;

public interface PostMemberWishPort {

  PostMemberWishesView read(UUID memberId);

  boolean exists(UUID memberID);
}
