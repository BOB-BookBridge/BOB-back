package com.bob.web.file.adapter.in;

import static com.bob.support.fixture.command.CreatePostCommandFixture.FILE_NAMES;
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

@DisplayName("ChatFileAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class ChatFileAdapterTest {

  @InjectMocks
  private ChatFileAdapter adapter;

  @Mock
  private FileModifyUseCase fileModifyUseCase;

  @Test
  @DisplayName("채팅 파일 referenceId 변경 호출 테스트")
  void 채팅_파일의_referenceId를_변경할_수_있다() {
    // given
    String referenceId = "chat-123";

    // when
    adapter.modifyReferenceId(FILE_NAMES, referenceId);

    // then
    verify(fileModifyUseCase, times(1)).modifyReferenceIdProcess(any(ModifyReferenceIdCommand.class));
  }
}
