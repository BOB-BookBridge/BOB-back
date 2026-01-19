package com.bob.core.inquiry.domain.repository;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.dsl.InquiryQueryRepository;

public interface InquiryRepository extends CrudRepository<Inquiry, Long>, InquiryQueryRepository {

}
