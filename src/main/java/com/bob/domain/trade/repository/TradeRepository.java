package com.bob.domain.trade.repository;

import com.bob.domain.trade.entity.Trade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TradeRepository extends CrudRepository<Trade, Long>, CustomTradeRepository {

  @Query("""
      SELECT t FROM Trade t
      WHERE t.postId = :postId
      ORDER BY
        CASE t.status
          WHEN com.bob.domain.trade.entity.status.Status.COMPLETED THEN 0
          WHEN com.bob.domain.trade.entity.status.Status.RESERVED THEN 1
          WHEN com.bob.domain.trade.entity.status.Status.REQUESTED THEN 2
          WHEN com.bob.domain.trade.entity.status.Status.CANCELED THEN 3
        END
      """)
  List<Trade> findAllByPostId(Long postId);

  @Query("""
      SELECT t.id FROM Trade t
       WHERE t.postId = :postId
         AND t.buyerId = :buyerId
      """)
  Optional<Long> findIdByPostIdAndBuyerId(Long postId, UUID buyerId);
}
