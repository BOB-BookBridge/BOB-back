package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;

import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.repository.TradeItemRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TradeItemService {

  private final TradeItemRepository tradeItemRepository;

  @Transactional
  public void createTradeItemsProcess(CreateTradeItemsCommand command) {
    List<TradeItem> toSave = command.itemIds().stream()
        .map(id -> TradeItem.create(command.tradeId(), id, command.owner()))
        .toList();
    tradeItemRepository.saveAll(toSave);
  }

  @Transactional(readOnly = true)
  public List<Long> readTradeItemIdsProcess(Long tradeId, Owner owner) {
    return tradeItemRepository.findAllByTradeIdAndOwner(tradeId, owner).stream().map(TradeItem::getItemId).toList();
  }

  @Transactional(readOnly = true)
  public Map<Owner, List<Long>> readTradeItemsOwnerMapProcess(Long tradeId) {
    Map<Owner, List<Long>> result = new EnumMap<>(Owner.class);
    result.put(SELLER, readTradeItemIdsProcess(tradeId, SELLER));
    result.put(BUYER,  readTradeItemIdsProcess(tradeId, BUYER));
    return result;
  }

  @Transactional
  public void changeTradeItemsProcess(ChangeTradeItemsCommand command, Owner owner) {
    Long tradeId = command.tradeId();
    List<Long> current = tradeItemRepository.findAllItemId(tradeId, owner);
    List<Long> requested = command.itemIds();
    removeObsoleteItems(tradeId, owner, current, requested);
    insertNewItems(tradeId, owner, current, requested);
  }

  private void removeObsoleteItems(Long tradeId, Owner owner, List<Long> current, List<Long> requested) {
    List<Long> toRemove = new ArrayList<>(current);
    toRemove.removeAll(requested);
    tradeItemRepository.deleteTradeItem(tradeId, owner, toRemove);
    // TODO : toRemove item 사용 가능 전환
  }

  private void insertNewItems(Long tradeId, Owner owner, List<Long> current, List<Long> requested) {
    List<Long> toAdd = new ArrayList<>(requested);
    toAdd.removeAll(current);
    List<TradeItem> newItems = toAdd.stream().distinct()
        .map(id -> TradeItem.create(tradeId, id, owner))
        .toList();
    tradeItemRepository.saveAll(newItems);
    // TODO : toAdd item 사용 불가 전환
  }
}
