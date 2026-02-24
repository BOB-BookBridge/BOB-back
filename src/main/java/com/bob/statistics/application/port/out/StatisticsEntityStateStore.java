package com.bob.statistics.application.port.out;

import java.time.LocalDate;
import java.util.Optional;

public interface StatisticsEntityStateStore {

    Optional<EntityState> read(String domain, String entityId);

    void save(EntityState state);

    record EntityState(
        String domain,
        String entityId,
        LocalDate cohortDate,
        String currentStatus,
        boolean createdCounted
    ) {

    }
}
