package com.bob.core.application.chat.port.out;

import com.bob.core.application.chat.port.result.ChatPost;

public interface ChatPostPort {

    ChatPost read(Long postId);
}
