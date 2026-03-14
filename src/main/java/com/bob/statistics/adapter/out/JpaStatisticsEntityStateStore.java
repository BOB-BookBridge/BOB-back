package com.bob.statistics.adapter.out;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.domain.StatisticsEntityState;
import com.bob.statistics.domain.repository.StatisticsEntityStateRepository;

@Repository
@RequiredArgsConstructor
public class JpaStatisticsEntityStateStore implements StatisticsEntityStateStore {

    private final StatisticsEntityStateRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<EntityState> read(String domain, String entityId) {
        return repository.findByDomainAndEntityId(domain, entityId)
            .map(entity -> new EntityState(
                entity.getDomain(),
                entity.getEntityId(),
                entity.getCohortDate(),
                entity.getCurrentStatus(),
                entity.isCreatedCounted()
            ));
    }

    @Override
    @Transactional
    public void save(EntityState state) {
        StatisticsEntityState entity = repository.findByDomainAndEntityId(state.domain(), state.entityId())
            .orElse(StatisticsEntityState.builder()
                .domain(state.domain())
                .entityId(state.entityId())
                .cohortDate(state.cohortDate())
                .currentStatus(state.currentStatus())
                .createdCounted(state.createdCounted())
                .updatedAt(LocalDateTime.now())
                .build()
            );

        entity.update(state.cohortDate(), state.currentStatus(), state.createdCounted(), LocalDateTime.now());

        repository.save(entity);
    }
}
