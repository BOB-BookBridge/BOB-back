package com.bob.web.file.adapter.in;

import static com.bob.support.fixture.response.PostFileSummaryResponseFixture.DEFAULT_READ_FILES_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.file.service.dto.command.ModifyReferenceIdCommand;
import com.bob.domain.file.service.dto.query.ReadFilesWithDomainIdQuery;
import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.usecase.FileModifyUseCase;
import com.bob.domain.file.usecase.FileReadUseCase;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PostFileAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class PostFileAdapterTest {

  @InjectMocks
  private PostFileAdapter adapter;

  @Mock
  private FileModifyUseCase fileModifyUseCase;

  @Mock
  private FileReadUseCase fileReadUseCase;

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

  @Test
  @DisplayName("게시글 파일 요약 조회 테스트")
  void 게시글의_파일들을_요약_형태로_조회할_수_있다() {
    // given
    Long postId = 1L;
    FilesResponse mockResponse = DEFAULT_READ_FILES_RESPONSE;
    given(fileReadUseCase.readFilesByDomainId(any(ReadFilesWithDomainIdQuery.class)))
        .willReturn(mockResponse);

    // when
    PostFileSummaryResponse result = adapter.readPostFileSummaries(postId);

    // then
    assertThat(result.images()).hasSize(mockResponse.summaries().size());
    assertThat(result.images().get(0).fileName()).isEqualTo(mockResponse.summaries().get(0).fileName());
  }
}
