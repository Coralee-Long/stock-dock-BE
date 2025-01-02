package com.stockdock.repos;

import com.stockdock.models.CurrentStock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrentStockRepo extends MongoRepository<CurrentStock, String> {
}
