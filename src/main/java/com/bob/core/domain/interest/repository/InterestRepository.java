package com.bob.core.domain.interest.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.interest.Interest;

public interface InterestRepository extends CrudRepository<Interest, Long> {

    Optional<Interest> findByName(String name);
}
