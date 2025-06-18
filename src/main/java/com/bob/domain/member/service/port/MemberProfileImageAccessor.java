package com.bob.domain.member.service.port;

public interface MemberProfileImageAccessor {

  String getImageUploadUrl(String imageName, String contentType);
}
