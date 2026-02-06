package com.bob.admin.filter.application.port.out;

import java.util.List;
import java.util.Optional;

import com.bob.admin.filter.domain.ManagementFilterWord;

public interface FilterWordManager {

    ManagementFilterWord save(ManagementFilterWord filterWord);

    List<ManagementFilterWord> findAll();

    Optional<ManagementFilterWord> findById(Long id);

    boolean existsByWord(String word);

    void remove(ManagementFilterWord filterWord);
}
