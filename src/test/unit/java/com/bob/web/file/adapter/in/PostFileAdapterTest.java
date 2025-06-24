package com.bob.web.file.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.usecase.FileModifyUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PostFileAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class PostFileAdapterTest {

  @Mock
  private FileModifyUseCase fileModifyUseCase;

  @InjectMocks
  private PostFileAdapter adapter;

  @Test
  @DisplayName("게시글 이미지 referenceId 변경 호출 테스트")
  void 게시글_파일의_referenceId를_변경할_수_있다() {
    // given
    String oldRefId = "temporary-uuid";
    Long newRefId = 123L;

    // when
    adapter.modifyReferenceId(oldRefId, newRefId);

    // then
    verify(fileModifyUseCase, times(1)).modifyReferenceIdProcess(any(ModifyReferenceIdCommand.class));
  }
}
