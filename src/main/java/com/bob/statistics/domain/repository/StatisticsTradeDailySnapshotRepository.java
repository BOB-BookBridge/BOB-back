package com.bob.statistics.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsTradeDailySnapshot;

public interface StatisticsTradeDailySnapshotRepository extends JpaRepository<StatisticsTradeDailySnapshot, Long> {

    Optional<StatisticsTradeDailySnapshot> findBySnapshotDate(LocalDate snapshotDate);

    List<StatisticsTradeDailySnapshot> findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(LocalDate from, LocalDate to);
}
