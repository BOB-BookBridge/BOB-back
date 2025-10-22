package com.bob.web.member.request;

import java.util.List;

public record ReadMemberBooksRequest(
    String key,
    List<Long> require
) {

}
