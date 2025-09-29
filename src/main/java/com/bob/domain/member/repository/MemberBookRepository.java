package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberBook;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface MemberBookRepository extends CrudRepository<MemberBook, Long> {

  List<MemberBook> findByMemberId(UUID memberId);

  List<MemberBook> findAllByIdIn(List<Long> ids);
}
