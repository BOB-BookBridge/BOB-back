package com.bob.core.domain.report.repository;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.report.Report;

public interface ReportRepository extends CrudRepository<Report, Long> {

}
