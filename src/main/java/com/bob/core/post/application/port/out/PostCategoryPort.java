package com.bob.core.post.application.port.out;

import java.util.List;

public interface PostCategoryPort {

    List<Integer> readChildIds(Integer categoryId);
}
