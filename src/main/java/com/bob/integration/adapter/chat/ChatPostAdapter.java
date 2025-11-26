package com.bob.integration.adapter.chat;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.chat.port.out.ChatPostPort;
import com.bob.core.application.chat.port.result.ChatPost;
import com.bob.core.application.post.dto.query.ReadPostDetailQuery;
import com.bob.core.application.post.dto.result.PostDetail;
import com.bob.core.application.post.port.in.PostReader;

@Component
@RequiredArgsConstructor
public class ChatPostAdapter implements ChatPostPort {

    private final PostReader postReader;

    @Override
    public ChatPost read(Long postId) {
        ReadPostDetailQuery query = new ReadPostDetailQuery(null, false);

        PostDetail detail = postReader.readDetail(postId, query);

        return ChatPost.builder()
            .id(detail.id())
            .tradeStatus(detail.tradeStatus())
            .sellerId(detail.writer().id())
            .title(detail.title())
            .thumbnailUrl(detail.thumbnailUrl())
            .sellPrice(detail.price())
            .build();
    }
}
