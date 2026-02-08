package com.bob.core.report.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.dsl.ReportQueryRepository;
import com.bob.core.report.domain.repository.projection.ReportCount;

public interface ReportRepository extends CrudRepository<Report, Long>, ReportQueryRepository {

    List<Report> findAllByReportedId(UUID reportedId);

    List<Report> findAllByTargetAndTargetId(ReportTarget target, Long targetId);

    List<Report> findAllByTargetAndTargetIdAndIdNot(ReportTarget target, Long targetId, Long id);

    @Query("""
          SELECT r.reportedId AS reportedId, COUNT(r) AS count
            FROM Report r
           WHERE r.reportedId IN :reportedIds AND r.status = "PROCESSED"
           GROUP BY r.reportedId
        """
    )
    List<ReportCount> countProcessedByReportedIds(List<UUID> reportedIds);
}
