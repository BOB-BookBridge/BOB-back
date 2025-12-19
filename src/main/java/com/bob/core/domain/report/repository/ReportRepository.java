package com.bob.core.domain.report.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.report.Report;
import com.bob.core.domain.report.repository.projection.ReportCount;

public interface ReportRepository extends CrudRepository<Report, Long> {

    @Query("""
          SELECT r.reportedId AS reportedId, COUNT(r) AS count
            FROM Report r
           WHERE r.reportedId IN :reportedIds
           GROUP BY r.reportedId
        """
    )
    List<ReportCount> countByReportedIds(List<UUID> reportedIds);
}
