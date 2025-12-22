package com.bob.core.interest.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.interest.domain.Interest;

public interface InterestRepository extends CrudRepository<Interest, Long> {

    Optional<Interest> findByName(String name);
}
