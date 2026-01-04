package com.bob.integration.adapter.chat;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.chat.application.port.out.ChatPostPort;
import com.bob.core.chat.application.port.result.ChatPost;
import com.bob.core.post.application.dto.result.PostBasicInfo;
import com.bob.core.post.application.port.in.PostReader;

@Component
@RequiredArgsConstructor
public class ChatPostAdapter implements ChatPostPort {

    private final PostReader postReader;

    @Override
    public ChatPost read(Long postId) {
        PostBasicInfo info = postReader.readBasicInfo(postId);

        return ChatPost.builder()
            .id(info.id())
            .tradeStatus(info.tradeStatus())
            .sellerId(info.sellerId())
            .title(info.title())
            .thumbnailUrl(info.thumbnailUrl())
            .sellPrice(info.price())
            .build();
    }
}
