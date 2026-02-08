package com.bob.infrastructure.data.filter.adapter;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.admin.filter.application.port.out.FilterWordManager;
import com.bob.admin.filter.domain.ManagementFilterWord;
import com.bob.infrastructure.data.filter.model.FilterWord;
import com.bob.infrastructure.data.filter.repository.FilterWordRepository;

@Component
@RequiredArgsConstructor
public class FilterWordManagerAdapter implements FilterWordManager {

    private final FilterWordRepository filterWordRepository;

    @Override
    public ManagementFilterWord save(ManagementFilterWord filterWord) {
        FilterWord entity = FilterWord.builder()
            .word(filterWord.getWord())
            .predefined(filterWord.isPredefined())
            .createdAt(filterWord.getCreatedAt())
            .build();

        FilterWord saved = filterWordRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public List<ManagementFilterWord> findAll() {
        return filterWordRepository.findAllByPredefinedFalse().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public Optional<ManagementFilterWord> findById(Long id) {
        return filterWordRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public boolean existsByWord(String word) {
        return filterWordRepository.existsByWord(word);
    }

    @Override
    public void remove(ManagementFilterWord filterWord) {
        filterWordRepository.deleteById(filterWord.getId());
    }

    private ManagementFilterWord toDomain(FilterWord entity) {
        return ManagementFilterWord.builder()
            .id(entity.getId())
            .word(entity.getWord())
            .predefined(entity.isPredefined())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
