package com.bob.domain.chat.service.dto.response;

import com.bob.domain.chat.service.dto.response.internal.MessageSummary;
import java.util.List;

public record ChatMessagesResponse(
    List<MessageSummary> messages,
    boolean hasNext
) {

}
