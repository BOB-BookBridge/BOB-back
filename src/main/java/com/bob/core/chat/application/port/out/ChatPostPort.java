package com.bob.core.chat.application.port.out;

import com.bob.core.chat.application.port.result.ChatPost;

public interface ChatPostPort {

    ChatPost read(Long postId);
}
