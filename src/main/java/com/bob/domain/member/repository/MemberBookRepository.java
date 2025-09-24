package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberBook;
import org.springframework.data.repository.CrudRepository;

public interface MemberBookRepository extends CrudRepository<MemberBook, Long> {

}
