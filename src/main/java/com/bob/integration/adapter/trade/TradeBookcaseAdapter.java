package com.bob.integration.adapter.trade;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.bookcase.application.dto.command.AllocateUsageCommand;
import com.bob.core.bookcase.application.dto.command.FreeUsageCommand;
import com.bob.core.bookcase.application.dto.result.BookcaseItemDetail;
import com.bob.core.bookcase.application.port.in.BookcaseDeleter;
import com.bob.core.bookcase.application.port.in.BookcaseModifier;
import com.bob.core.bookcase.application.port.in.BookcaseReader;
import com.bob.core.trade.application.port.out.TradeBookcasePort;
import com.bob.core.trade.application.port.result.TradeBookcaseItem;

@Component
@RequiredArgsConstructor
public class TradeBookcaseAdapter implements TradeBookcasePort {

    private final BookcaseReader bookcaseReader;
    private final BookcaseModifier bookcaseModifier;
    private final BookcaseDeleter bookcaseDeleter;

    @Override
    public TradeBookcaseItem read(Long id) {
        List<BookcaseItemDetail> result = bookcaseReader.readItemDetails(List.of(id));

        return convert(result).get(0);
    }

    @Override
    public List<TradeBookcaseItem> read(List<Long> ids) {
        List<BookcaseItemDetail> result = bookcaseReader.readItemDetails(ids);

        return convert(result);
    }

    private List<TradeBookcaseItem> convert(List<BookcaseItemDetail> details) {
        return details.stream().map(i -> TradeBookcaseItem.builder()
            .id(i.id())
            .bookId(i.bookId())
            .status(i.status())
            .title(i.title())
            .author(i.author())
            .priceStandard(i.priceStandard())
            .cover(i.cover())
            .pubDate(i.pubDate())
            .available(i.available())
            .build()
        ).toList();
    }

    @Override
    public void allocateUsageWithAuth(List<Long> ids, UUID memberId, Long usageId) {
        AllocateUsageCommand command = AllocateUsageCommand.of(memberId, usageId);

        bookcaseModifier.allocate(ids, command);
    }

    @Override
    public void freeUsage(List<Long> ids) {
        FreeUsageCommand command = FreeUsageCommand.of(null);

        bookcaseModifier.free(ids, command);
    }

    @Override
    public void freeUsageWithAuth(List<Long> ids, UUID memberId) {
        FreeUsageCommand command = FreeUsageCommand.of(memberId);

        bookcaseModifier.free(ids, command);
    }

    @Override
    public void delete(List<Long> ids) {
        bookcaseDeleter.deleteItems(ids);
    }
}
