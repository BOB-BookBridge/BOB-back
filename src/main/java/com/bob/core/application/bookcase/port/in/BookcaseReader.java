package com.bob.core.application.bookcase.port.in;

import java.util.List;

import com.bob.core.application.bookcase.dto.query.ReadBookcaseQuery;
import com.bob.core.application.bookcase.dto.result.BookcaseItemDetail;
import com.bob.core.domain.bookcase.BookcaseItem;

public interface BookcaseReader {

    BookcaseItem read(Long id);

    List<BookcaseItem> readItems(List<Long> ids);

    List<BookcaseItemDetail> readItemDetails(List<Long> ids);

    List<BookcaseItemDetail> readItemDetailsByQuery(ReadBookcaseQuery query);
}
