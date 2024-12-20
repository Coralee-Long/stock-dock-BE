package com.stockdock.repos;

import com.stockdock.models.ETF;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for performing database operations on ETF entities.
 * Extends {@link MongoRepository} to provide CRUD operations.
 */
public interface ETFRepo extends MongoRepository<ETF, String> {
}
