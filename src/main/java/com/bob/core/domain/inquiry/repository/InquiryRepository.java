package com.bob.core.domain.inquiry.repository;

import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.inquiry.Inquiry;

public interface InquiryRepository extends CrudRepository<Inquiry, Long> {

}
