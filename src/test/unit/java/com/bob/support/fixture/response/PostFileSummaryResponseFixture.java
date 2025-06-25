package com.bob.support.fixture.response;

import com.bob.domain.file.service.dto.response.FilesResponse;
import com.bob.domain.file.service.dto.response.internal.FileSummaryResponse;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse.FileSummary;
import java.util.List;

public class PostFileSummaryResponseFixture {

  public static final PostFileSummaryResponse DEFAULT_POST_FILE_SUMMARIES = PostFileSummaryResponse.builder()
      .images(List.of(
          new FileSummary(0, "post/0197a143-7979-7d70-a2e3-8d51a56fc86a.png"),
          new FileSummary(1, "post/0197a143-7979-7df1-825a-1c20433fd532.jpg"),
          new FileSummary(2, "post/0197a143-7979-7f02-9572-2f066b3c1796.gif")
      ))
      .build();

  public static final FilesResponse DEFAULT_READ_FILES_RESPONSE = new FilesResponse(
      List.of(
          new FileSummaryResponse(0, "post/0197a143-7979-7d70-a2e3-8d51a56fc86a.png"),
          new FileSummaryResponse(1, "post/0197a143-7979-7df1-825a-1c20433fd532.jpg"),
          new FileSummaryResponse(2, "post/0197a143-7979-7f02-9572-2f066b3c1796.gif")
      )
  );
}
