package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberWish;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface MemberWishRepository extends CrudRepository<MemberWish, Long> {

  List<MemberWish> findAllByMemberId(UUID memberId);

  boolean existsByMemberIdAndBookId(UUID memberId, Long bookId);
}
