package com.bob.admin.filter.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.filter.application.port.in.ManagementFilterWordReader;
import com.bob.admin.filter.application.port.out.FilterWordManager;
import com.bob.admin.filter.domain.ManagementFilterWord;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementFilterWordQueryService implements ManagementFilterWordReader {

    private final FilterWordManager filterWordManager;

    @Override
    public List<ManagementFilterWord> readAll() {
        return filterWordManager.findAll();
    }

    @Override
    public ManagementFilterWord read(Long wordId) {
        return filterWordManager.findById(wordId)
            .orElseThrow(() -> new IllegalArgumentException("금칙어를 찾을 수 없습니다. id: " + wordId));
    }
}
