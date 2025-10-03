package com.bob.domain.trade.service.reader;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TradeReader {

  private final TradeRepository tradeRepository;

  public List<Trade> readTradesByQuery(ReadTradesQuery query, Pageable pageable) {
    return tradeRepository.findTradesByQuery(query, pageable);
  }

  public List<Trade> readTradesByPostId(Long postId) {
    return tradeRepository.findAllByPostId(postId);
  }

  public Trade readTradeById(Long id) {
    return tradeRepository.findById(id)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_TRADE));
  }
}
