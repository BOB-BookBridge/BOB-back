package com.bob.integration.adapter.post;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.category.port.in.CategoryReader;
import com.bob.core.application.post.port.out.PostCategoryPort;

@Component
@RequiredArgsConstructor
public class PostCategoryAdapter implements PostCategoryPort {

    private final CategoryReader categoryReader;

    public List<Integer> readChildIds(Integer categoryId) {
        return categoryReader.readChildCategoryIds(categoryId);
    }
}
