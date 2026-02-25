package com.bob.statistics.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.statistics.domain.StatisticsMemberDailySnapshot;

public interface StatisticsMemberDailySnapshotRepository extends JpaRepository<StatisticsMemberDailySnapshot, Long> {

    Optional<StatisticsMemberDailySnapshot> findBySnapshotDate(LocalDate snapshotDate);

    List<StatisticsMemberDailySnapshot> findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(LocalDate from,
        LocalDate to);
}
