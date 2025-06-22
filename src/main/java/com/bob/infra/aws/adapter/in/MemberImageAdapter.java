package com.bob.infra.aws.adapter.in;

import com.bob.domain.member.service.port.out.MemberProfileImageAccessor;
import com.bob.infra.aws.service.usecase.ImageUrlReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberImageAdapter implements MemberProfileImageAccessor {

  private final ImageUrlReadUseCase readUseCase;

  @Override
  public String generateMemberProfileImageUploadUrl(String imageName, String contentType) {
    return readUseCase.generateImageUploadUrlProcess(imageName, contentType);
  }
}
