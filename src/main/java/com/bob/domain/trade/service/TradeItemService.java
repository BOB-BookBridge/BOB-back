package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;

import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.repository.TradeItemRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.port.out.TradeMemberBookPort;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TradeItemService {

  private final TradeItemRepository tradeItemRepository;

  private final TradeMemberPort memberPort;
  private final TradeMemberBookPort memberBookPort;

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
    result.put(BUYER, readTradeItemIdsProcess(tradeId, BUYER));
    return result;
  }

  @Transactional
  public void changeTradeItemsProcess(ChangeTradeItemsCommand command, Long postId, Owner owner) {
    List<Long> current = tradeItemRepository.findAllItemId(command.tradeId(), owner);
    List<Long> requested = command.itemIds();
    verifyItemsChange(current, requested);

    removeObsoleteItems(command.memberId(), command.tradeId(), postId, owner, current, requested);
    insertNewItems(command.memberId(), command.tradeId(), postId, owner, current, requested);
  }

  private void removeObsoleteItems(UUID memberId, Long tradeId, Long postId, Owner owner, List<Long> current, List<Long> requested) {
    List<Long> toRemove = new ArrayList<>(current);
    toRemove.removeAll(requested);
    tradeItemRepository.deleteTradeItem(tradeId, owner, toRemove);
    memberPort.changeMemberBookUsage(memberId, postId, toRemove, true);
  }

  private void insertNewItems(UUID memberId, Long tradeId, Long postId, Owner owner, List<Long> current, List<Long> requested) {
    List<Long> toAdd = new ArrayList<>(requested);
    memberPort.changeMemberBookUsage(memberId, postId, toAdd, false);

    toAdd.removeAll(current);
    List<TradeItem> newItems = toAdd.stream().distinct()
        .map(id -> TradeItem.create(tradeId, id, owner))
        .toList();
    tradeItemRepository.saveAll(newItems);
  }

  private void verifyItemsChange(List<Long> origin, List<Long> other) {
    if (new HashSet<>(origin).equals(new HashSet<>(other))) {
      throw new ApplicationException(ApplicationError.TRADE_ITEMS_UNCHANGED);
    }
  }

  @Transactional
  public void freeTraderItemsExcludeMainItem(Long tradeId, Long mainItemId) {
    List<Long> itemIds = tradeItemRepository.findItemIdsByTradeId(tradeId);
    itemIds.remove(mainItemId);
    memberBookPort.freeUsage(itemIds);
  }

  @Transactional
  public void freeOtherTraderItems(Long postId, Long tradeId, Long mainItemId) {
    List<Long> itemIds = tradeItemRepository.findItemIdsExcludeMainItemByPost(postId, tradeId, mainItemId);
    memberBookPort.freeUsage(itemIds);
  }

  @Transactional
  public void removeTradeItems(Long tradeId) {
    tradeItemRepository.deleteTradeItemsByTradeId(tradeId);
  }

  @Transactional
  public void removeTraderItems(Long tradeId) {
    List<Long> itemIds = tradeItemRepository.findItemIdsByTradeId(tradeId);
    memberBookPort.remove(itemIds);
  }
}
