package com.bob.core.post.application.port.out.infra;

import java.util.List;

public interface PostFilterPort {

    List<String> filter(String content);
}
