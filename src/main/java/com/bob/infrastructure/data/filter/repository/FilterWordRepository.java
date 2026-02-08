package com.bob.infrastructure.data.filter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.infrastructure.data.filter.model.FilterWord;

public interface FilterWordRepository extends JpaRepository<FilterWord, Long> {

    boolean existsByWord(String word);

    List<FilterWord> findAllByPredefinedFalse();
}
