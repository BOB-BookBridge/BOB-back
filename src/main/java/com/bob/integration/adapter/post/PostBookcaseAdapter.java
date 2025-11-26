package com.bob.integration.adapter.post;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.bookcase.dto.command.AllocateUsageCommand;
import com.bob.core.application.bookcase.dto.command.FreeUsageByRefIdCommand;
import com.bob.core.application.bookcase.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.application.bookcase.port.in.BookcaseModifier;
import com.bob.core.application.bookcase.port.in.BookcaseRegister;
import com.bob.core.application.post.port.out.PostBookcasePort;
import com.bob.core.application.post.port.out.request.RegisterBookcaseRequest;
import com.bob.core.application.post.port.result.PostBookcaseId;
import com.bob.core.domain.bookcase.BookcaseItem;

@Component
@RequiredArgsConstructor
public class PostBookcaseAdapter implements PostBookcasePort {

    private final BookcaseRegister bookcaseRegister;
    private final BookcaseModifier bookcaseModifier;

    @Override
    public PostBookcaseId register(RegisterBookcaseRequest request) {
        RegisterBookcaseItemCommand command = RegisterBookcaseItemCommand.of(
            request.memberId(), request.status(), request.isbn(), request.title(), request.author(),
            request.description(), request.priceStandard(), request.cover(), request.pubDate()
        );

        BookcaseItem item = bookcaseRegister.registerItem(command);

        return PostBookcaseId.of(item.getId(), item.getBookId());
    }

    @Override
    public void allocate(UUID memberId, Long usageId, Long bookcaseItemId) {
        AllocateUsageCommand command = AllocateUsageCommand.of(memberId, usageId);
        bookcaseModifier.allocate(List.of(bookcaseItemId), command);
    }

    @Override
    public void free(Long refId) {
        FreeUsageByRefIdCommand command = FreeUsageByRefIdCommand.of(refId);
        bookcaseModifier.freeByRefId(command);
    }
}
