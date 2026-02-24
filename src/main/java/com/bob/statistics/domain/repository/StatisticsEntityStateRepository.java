package com.bob.statistics.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsEntityState;

public interface StatisticsEntityStateRepository extends JpaRepository<StatisticsEntityState, Long> {

    Optional<StatisticsEntityState> findByDomainAndEntityId(String domain, String entityId);
}
