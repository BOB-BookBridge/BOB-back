package com.bob.statistics.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsPostDailySnapshot;

public interface StatisticsPostDailySnapshotRepository extends JpaRepository<StatisticsPostDailySnapshot, Long> {

    Optional<StatisticsPostDailySnapshot> findBySnapshotDate(LocalDate snapshotDate);

    List<StatisticsPostDailySnapshot> findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(LocalDate from, LocalDate to);
}
