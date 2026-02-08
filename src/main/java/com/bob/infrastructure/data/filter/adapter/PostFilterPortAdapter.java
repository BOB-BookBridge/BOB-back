package com.bob.infrastructure.data.filter.adapter;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.post.application.port.out.infra.PostFilterPort;
import com.bob.infrastructure.data.filter.model.FilterWord;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;

@Component
@RequiredArgsConstructor
public class PostFilterPortAdapter implements PostFilterPort {

    private final FilterWordRepository filterWordRepository;

    @Override
    public List<String> filter(String content) {
        if (content == null)
            return Collections.emptyList();

        String lowerContent = content.toLowerCase();

        return filterWordRepository.findAll().stream()
            .map(FilterWord::getWord)
            .filter(word -> lowerContent.contains(word.toLowerCase()))
            .toList();
    }
}
