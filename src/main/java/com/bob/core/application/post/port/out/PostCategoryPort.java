package com.bob.core.application.post.port.out;

import java.util.List;

public interface PostCategoryPort {

    List<Integer> readChildIds(Integer categoryId);
}
