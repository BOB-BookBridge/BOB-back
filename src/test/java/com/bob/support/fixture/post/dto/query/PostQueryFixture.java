package com.bob.support.fixture.post.dto.query;

import java.util.ArrayList;
import java.util.List;

import com.bob.core.domain.post.repository.dsl.query.ReadPostsQuery;
import com.bob.core.domain.post.repository.dsl.query.SearchKey;
import com.bob.core.domain.post.repository.dsl.query.SearchPrice;
import com.bob.core.domain.post.repository.dsl.query.SortKey;

public class PostQueryFixture {

    public static ReadPostsQuery defaultReadFilteredPostsQuery() {
        return ReadPostsQuery.builder()
            .key(SearchKey.ALL)
            .keyword(null)
            .emdId(null)
            .categoryIds(new ArrayList<>())
            .price(null)
            .postStatus(null)
            .bookStatus(null)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchTitleQuery() {
        return ReadPostsQuery.builder()
            .key(SearchKey.TITLE)
            .keyword("오브젝트")
            .bookIds(new ArrayList<>())
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchAuthorQuery() {
        return ReadPostsQuery.builder()
            .key(SearchKey.AUTHOR)
            .keyword("김영한")
            .bookIds(new ArrayList<>())
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchUnder5000PriceQuery() {
        return ReadPostsQuery.builder()
            .price(SearchPrice.UNDER_5000)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchBetween_5000_10000_PriceQuery() {
        return ReadPostsQuery.builder()
            .price(SearchPrice.BETWEEN_5000_AND_10000)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchCategoryQuery() {
        return ReadPostsQuery.builder()
            .categoryIds(new ArrayList<>(List.of(1)))
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchTradeStatusQuery() {
        return ReadPostsQuery.builder()
            .postStatus("COMPLETED")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchBookStatusQuery() {
        return ReadPostsQuery.builder()
            .bookStatus("BEST")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchNewestQuery() {
        return ReadPostsQuery.builder()
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchOldestQuery() {
        return ReadPostsQuery.builder()
            .sortKey(SortKey.OLD)
            .build();
    }

    public static ReadPostsQuery searchLowPriceQuery() {
        return ReadPostsQuery.builder()
            .sortKey(SortKey.LOW_PRICE)
            .build();
    }

    public static ReadPostsQuery searchHighPriceQuery() {
        return ReadPostsQuery.builder()
            .sortKey(SortKey.HIGH_PRICE)
            .build();
    }

    public static ReadPostsQuery searchBetween_10000_20000_PriceQuery() {
        return ReadPostsQuery.builder()
            .price(SearchPrice.BETWEEN_10000_AND_20000)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchOver20000PriceQuery() {
        return ReadPostsQuery.builder()
            .price(SearchPrice.OVER_20000)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchByEmdIdQuery(Integer emdId) {
        return ReadPostsQuery.builder()
            .emdId(emdId)
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchHighBookStatusQuery() {
        return ReadPostsQuery.builder()
            .bookStatus("HIGH")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchMediumBookStatusQuery() {
        return ReadPostsQuery.builder()
            .bookStatus("MEDIUM")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchLowBookStatusQuery() {
        return ReadPostsQuery.builder()
            .bookStatus("LOW")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchReadyTradeStatusQuery() {
        return ReadPostsQuery.builder()
            .postStatus("READY")
            .sortKey(SortKey.RECENT)
            .build();
    }

    public static ReadPostsQuery searchReservedTradeStatusQuery() {
        return ReadPostsQuery.builder()
            .postStatus("RESERVED")
            .sortKey(SortKey.RECENT)
            .build();
    }
}
