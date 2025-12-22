package com.bob.core.post.application.port.out;

import java.util.UUID;

import com.bob.core.post.application.port.result.PostMember;

public interface PostMemberPort {

    PostMember read(UUID memberId);
}
