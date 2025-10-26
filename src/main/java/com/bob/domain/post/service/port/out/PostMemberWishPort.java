package com.bob.domain.post.service.port.out;

import java.util.UUID;

public interface PostMemberWishPort {

  boolean exists(UUID memberID);
}
