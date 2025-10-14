package com.bob.domain.trade.repository;

import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.entity.TradeItem;
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

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      DELETE FROM TradeItem ti
       WHERE ti.tradeId = :tradeId
         AND ti.owner = :owner
         AND ti.itemId in :itemIds
      """)
  void deleteTradeItem(Long tradeId, Owner owner, Collection<Long> itemIds);
}
