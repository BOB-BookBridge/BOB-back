package com.bob.core.post.application.port.out;

import com.bob.core.post.application.port.result.PostArea;

public interface PostAreaPort {

    PostArea read(int emdId);
}
