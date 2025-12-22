package com.bob.core.bookcase.application.port.in;

import java.util.List;

import com.bob.core.bookcase.application.dto.query.ReadBookcaseQuery;
import com.bob.core.bookcase.application.dto.result.BookcaseItemDetail;
import com.bob.core.bookcase.domain.BookcaseItem;

public interface BookcaseReader {

    BookcaseItem read(Long id);

    List<BookcaseItem> readItems(List<Long> ids);

    List<BookcaseItemDetail> readItemDetails(List<Long> ids);

    List<BookcaseItemDetail> readItemDetailsByQuery(ReadBookcaseQuery query);
}
