package com.stockdock.repos;

import com.stockdock.models.HistoricalStock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalStockRepo extends MongoRepository<HistoricalStock, String> {
   List<HistoricalStock> findBySymbol (String symbol);
}
