package com.bob.core.inquiry.domain.repository;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.inquiry.domain.Inquiry;

public interface InquiryRepository extends CrudRepository<Inquiry, Long> {

}
