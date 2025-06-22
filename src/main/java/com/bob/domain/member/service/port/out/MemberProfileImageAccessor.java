package com.bob.domain.member.service.port.out;

public interface MemberProfileImageAccessor {

  String generateMemberProfileImageUploadUrl(String imageName, String contentType);
}
