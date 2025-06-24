package com.bob.domain.post.service.port.out;

public interface PostFilePort {

  void modifyReferenceId(String oldReferenceId, Long currentReferenceId);
}
