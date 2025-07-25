package com.bob.web.post.adapter.in;

import com.bob.domain.post.service.dto.command.ChangePostStatusCommand;
import com.bob.domain.post.service.dto.query.ReadPostDetailQuery;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.usecase.PostModifyUseCase;
import com.bob.domain.post.usecase.PostReadUseCase;
import com.bob.domain.trade.service.port.out.TradePostPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradePostAdapter implements TradePostPort {

  private final PostReadUseCase readUseCase;
  private final PostModifyUseCase modifyUseCase;

  @Override
  public PostDetailResponse readTradePostSummary(Long postId) {
    return readUseCase.readPostDetailProcess(ReadPostDetailQuery.of(null, postId, false));
  }

  @Override
  public void changePostStatus(Long postId, String status) {
    ChangePostStatusCommand command = new ChangePostStatusCommand(postId, status);
    modifyUseCase.changePostStatusProcess(command);
  }
}
