package com.bob.core.application.post.port.out;

import com.bob.core.application.post.port.result.PostArea;

public interface PostAreaPort {

    PostArea read(int emdId);
}
