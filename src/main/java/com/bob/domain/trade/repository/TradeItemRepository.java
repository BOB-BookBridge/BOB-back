package com.bob.domain.trade.repository;

import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.entity.type.Owner;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TradeItemRepository extends CrudRepository<TradeItem, Long> {

  List<TradeItem> findAllByTradeIdAndOwner(Long tradeId, Owner owner);

  @Query("""
      SELECT ti.itemId FROM TradeItem ti
       WHERE ti.tradeId = :tradeId
         AND ti.owner = :owner
      """)
  List<Long> findAllItemId(Long tradeId, Owner owner);

  @Query("""
      SELECT ti.itemId
        FROM TradeItem ti
       WHERE ti.tradeId = :tradeId
      """)
  List<Long> findItemIdsByTradeId(Long tradeId);

  @Query("""
      SELECT DISTINCT ti.itemId
        FROM TradeItem ti, Trade t
       WHERE ti.tradeId = t.id
         AND t.postId = :postId
         AND t.id <> :completedTradeId
         AND ti.itemId <> :mainItemId
      """)
  List<Long> findItemIdsExcludeMainItemByPost(Long postId, Long completedTradeId, Long mainItemId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      DELETE FROM TradeItem ti
       WHERE ti.tradeId = :tradeId
         AND ti.owner = :owner
         AND ti.itemId in :itemIds
      """)
  void deleteTradeItem(Long tradeId, Owner owner, Collection<Long> itemIds);
}
