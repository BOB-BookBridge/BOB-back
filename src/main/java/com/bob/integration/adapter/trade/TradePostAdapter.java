package com.bob.integration.adapter.trade;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.post.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.application.post.dto.query.ReadPostDetailQuery;
import com.bob.core.application.post.dto.result.PostDetail;
import com.bob.core.application.post.port.in.PostModifier;
import com.bob.core.application.post.port.in.PostReader;
import com.bob.core.application.trade.port.out.TradePostPort;
import com.bob.core.application.trade.port.result.TradePost;

@Component
@RequiredArgsConstructor
public class TradePostAdapter implements TradePostPort {

    private final PostReader postReader;
    private final PostModifier postModifier;

    @Override
    public TradePost read(Long postId) {
        ReadPostDetailQuery query = new ReadPostDetailQuery(null, false);

        PostDetail detail = postReader.readDetail(postId, query);

        return TradePost.builder()
            .id(detail.id())
            .status(detail.status())
            .tradeStatus(detail.tradeStatus())
            .sellerId(detail.writer().id())
            .sellerBookId(detail.writerBookId())
            .title(detail.title())
            .thumbnailUrl(detail.thumbnailUrl())
            .wishOnly(detail.wishOnly())
            .build();
    }

    @Override
    public void changeTradeProgress(Long postId, String status) {
        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand(status);

        postModifier.changePostTradeProgress(postId, command);
    }
}
