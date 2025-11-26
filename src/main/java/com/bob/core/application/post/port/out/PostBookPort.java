package com.bob.core.application.post.port.out;

import java.util.List;

import com.bob.core.application.post.port.result.PostBook;

public interface PostBookPort {

    PostBook read(Long bookId);

    List<Long> searchAllIds(String key, String keyword);
}
