package com.bob.core.post.application.port.out;

import java.util.List;

import com.bob.core.post.application.port.result.PostBook;

public interface PostBookPort {

    PostBook read(Long bookId);

    List<Long> searchAllIds(String key, String keyword);
}
