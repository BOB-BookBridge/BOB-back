package com.bob.domain.member.service.port.out;

public interface MemberProfileImageAccessor {

  String getImageUploadUrl(String imageName, String contentType);
}
