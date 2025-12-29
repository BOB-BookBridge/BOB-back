package com.bob.integration.adapter.trade;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.post.application.dto.result.PostBasicInfo;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.trade.application.port.out.TradePostPort;
import com.bob.core.trade.application.port.result.TradePost;

@Component
@RequiredArgsConstructor
public class TradePostAdapter implements TradePostPort {

    private final PostReader postReader;

    @Override
    public TradePost read(Long postId) {
        PostBasicInfo info = postReader.readBasicInfo(postId);

        return TradePost.builder()
            .id(info.id())
            .status(info.status())
            .tradeStatus(info.tradeStatus())
            .sellerId(info.sellerId())
            .sellerBookId(info.sellerBookId())
            .title(info.title())
            .thumbnailUrl(info.thumbnailUrl())
            .wishOnly(info.wishOnly())
            .build();
    }
}
