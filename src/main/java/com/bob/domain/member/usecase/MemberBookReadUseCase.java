package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;

public interface MemberBookReadUseCase {

  MemberBooksResponse readMemberBooksProcess(ReadMemberBooksQuery query);

  MemberBooksResponse readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery query);
}
