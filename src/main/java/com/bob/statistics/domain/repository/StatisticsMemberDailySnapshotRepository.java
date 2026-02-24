package com.bob.statistics.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsMemberDailySnapshot;

public interface StatisticsMemberDailySnapshotRepository extends JpaRepository<StatisticsMemberDailySnapshot, Long> {

    Optional<StatisticsMemberDailySnapshot> findBySnapshotDate(LocalDate snapshotDate);

    void deleteBySnapshotDate(LocalDate snapshotDate);
}
