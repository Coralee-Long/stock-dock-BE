package com.stockdock.repos;

import com.stockdock.models.CurrentStock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentStockRepo extends MongoRepository<CurrentStock, String> {
}
