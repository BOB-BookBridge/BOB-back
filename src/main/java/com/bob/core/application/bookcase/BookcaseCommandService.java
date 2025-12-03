package com.bob.core.application.bookcase;

import static com.bob.core.domain.bookcase.BookcaseItem.createBookcaseItem;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_ALREADY_USE;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_UNAVAILABLE;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_UNREMOVABLE;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.bookcase.dto.command.AllocateUsageCommand;
import com.bob.core.application.bookcase.dto.command.DeleteBookcaseItemCommand;
import com.bob.core.application.bookcase.dto.command.FreeUsageByRefIdCommand;
import com.bob.core.application.bookcase.dto.command.FreeUsageCommand;
import com.bob.core.application.bookcase.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.application.bookcase.port.in.BookcaseDeleter;
import com.bob.core.application.bookcase.port.in.BookcaseModifier;
import com.bob.core.application.bookcase.port.in.BookcaseReader;
import com.bob.core.application.bookcase.port.in.BookcaseRegister;
import com.bob.core.application.bookcase.port.out.BookcaseItemBookPort;
import com.bob.core.application.bookcase.port.result.BookcaseItemResult;
import com.bob.core.domain.bookcase.BookcaseItem;
import com.bob.core.domain.bookcase.repository.BookcaseItemRepository;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookcaseCommandService implements BookcaseRegister, BookcaseModifier, BookcaseDeleter {

    private final BookcaseItemRepository itemRepository;
    private final BookcaseReader itemReader;

    private final BookcaseItemBookPort bookPort;

    @Override
    public BookcaseItem registerItem(RegisterBookcaseItemCommand command) {
        Long bookId = createBook(command);

        BookcaseItem item = createBookcaseItem(command.memberId(), bookId, command.status());

        return itemRepository.save(item);
    }

    private Long createBook(RegisterBookcaseItemCommand command) {
        return bookPort.register(
            command.isbn(), command.title(), command.author(), command.description(),
            command.priceStandard(), command.cover(), command.pubDate()
        );
    }

    @Override
    public void allocate(List<Long> ids, AllocateUsageCommand command) {
        List<BookcaseItem> bookcase = itemReader.readItems(ids);

        verifyBookcaseOwner(command.memberId(), ids, bookcase);
        verifyBookcaseItemAvailable(bookcase);
        verifyBookcaseItemIsFree(bookcase, command.usageId());

        bookcase.stream()
            .filter(bc -> !Objects.equals(bc.getUsageId(), command.usageId()))
            .forEach(bc -> bc.updateUsageId(command.usageId()));
    }

    private void verifyBookcaseItemAvailable(List<BookcaseItem> bookcases) {
        bookcases.stream()
            .filter(BookcaseItem::isDeleted)
            .findFirst().ifPresent(item -> {
                BookcaseItemResult book = bookPort.readBook(item.getBookId());
                throw new ApplicationException(BOOKCASE_ITEM_UNAVAILABLE, book.title());
            });
    }

    private void verifyBookcaseItemIsFree(List<BookcaseItem> bookcases, Long usageId) {
        bookcases.stream()
            .filter(item -> item.getUsageId() != null && !Objects.equals(item.getUsageId(), usageId))
            .findFirst().ifPresent(bc -> {
                BookcaseItemResult book = bookPort.readBook(bc.getBookId());
                throw new ApplicationException(BOOKCASE_ITEM_ALREADY_USE, bc.getUsageId(), book.title());
            });
    }

    @Override
    public void free(List<Long> ids, FreeUsageCommand command) {
        verifyBookcaseOwner(command.memberId(), ids, itemReader.readItems(ids));

        itemRepository.freeUsageByIdIn(ids);
    }

    private static void verifyBookcaseOwner(UUID memberId, List<Long> requestIds, List<BookcaseItem> bookcase) {
        if (memberId == null)
            return;

        boolean isOwner = bookcase.stream().allMatch(bc -> Objects.equals(bc.getMemberId(), memberId));

        if (!isOwner || requestIds.size() != bookcase.size())
            throw new ApplicationException(BOOKCASE_ITEM_ACCESS_DENIED);
    }

    @Override
    public void freeByRefId(FreeUsageByRefIdCommand command) {
        itemRepository.freeUsageByUsageId(command.usageId());
    }

    @Override
    public void deleteItems(List<Long> ids) {
        List<BookcaseItem> bookcases = itemReader.readItems(ids);

        bookcases.forEach(BookcaseItem::delete);
    }

    @Override
    public void delete(Long id, DeleteBookcaseItemCommand command) {
        BookcaseItem item = itemReader.read(id);

        verifyItemOwn(command.memberId(), item);
        verifyItemDeletable(item);

        item.delete();
    }

    private static void verifyItemOwn(UUID memberId, BookcaseItem item) {
        if (!item.isOwner(memberId))
            throw new ApplicationException(BOOKCASE_ITEM_ACCESS_DENIED);
    }

    private static void verifyItemDeletable(BookcaseItem item) {
        if (!item.isDeletable())
            throw new ApplicationException(BOOKCASE_ITEM_UNREMOVABLE, item.getUsageId());
    }
}
