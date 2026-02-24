package com.bob.statistics.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsPostDailySnapshot;

public interface StatisticsPostDailySnapshotRepository extends JpaRepository<StatisticsPostDailySnapshot, Long> {

    Optional<StatisticsPostDailySnapshot> findBySnapshotDate(LocalDate snapshotDate);
}
