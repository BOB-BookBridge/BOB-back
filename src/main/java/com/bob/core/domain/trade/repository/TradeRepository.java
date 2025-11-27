package com.bob.core.domain.trade.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.dsl.CustomTradeRepository;

public interface TradeRepository extends CrudRepository<Trade, Long>, CustomTradeRepository {

    Optional<Trade> findByPostIdAndBuyerId(Long postId, UUID buyerId);

    @Query("""
        SELECT t FROM Trade t
        WHERE t.postId = :postId
        ORDER BY
          CASE t.status
            WHEN com.bob.core.domain.trade.status.Status.COMPLETED THEN 0
            WHEN com.bob.core.domain.trade.status.Status.RESERVED THEN 1
            WHEN com.bob.core.domain.trade.status.Status.REQUESTED THEN 2
            WHEN com.bob.core.domain.trade.status.Status.CANCELED THEN 3
          END
        """)
    List<Trade> findAllByPostId(Long postId);

    List<Trade> findAllByBuyerIdAndPostIdIn(UUID buyerId, List<Long> postIds);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Trade t SET t.status = com.bob.core.domain.trade.status.Status.CANCELED
         WHERE t.postId = :postId
           AND t.id <> :excludeId
           AND t.status NOT IN (com.bob.core.domain.trade.status.Status.CANCELED)
        """)
    void cancelOtherTrades(Long postId, Long excludeId);
}
