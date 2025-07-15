package com.bob.web.file.adapter.in;

import static com.bob.domain.file.entity.type.FileDomain.*;
import static com.bob.support.fixture.command.CreatePostCommandFixture.FILE_NAMES;
import static com.bob.support.fixture.response.ChatFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.file.entity.type.FileDomain;
import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
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
  private FileReadUseCase readUseCase;

  @Mock
  private FileModifyUseCase modifyUseCase;

  @Test
  @DisplayName("채팅 파일 조회 호출 테스트")
  void 채팅방의_파일_요약정보를_조회할_수_있다() {
    // given
    Long chatRoomId = 1L;
    FilesResponse fileSummary = DEFAULT_READ_FILES_RESPONSE;
    ReadFilesWithDomainIdQuery query = new ReadFilesWithDomainIdQuery(CHAT, chatRoomId.toString());

    given(readUseCase.readFilesByDomainId(query)).willReturn(fileSummary);

    // when
    FilesResponse actual = adapter.readChatFileSummaries(chatRoomId);

    // then
    assertThat(actual).isEqualTo(fileSummary);
    verify(readUseCase, times(1)).readFilesByDomainId(query);
  }

  @Test
  @DisplayName("채팅 파일 referenceId 변경 호출 테스트")
  void 채팅_파일의_referenceId를_변경할_수_있다() {
    // given
    String referenceId = "chat-123";

    // when
    adapter.modifyReferenceId(FILE_NAMES, referenceId);

    // then
    verify(modifyUseCase, times(1)).modifyReferenceIdProcess(any(ModifyReferenceIdCommand.class));
  }
}
