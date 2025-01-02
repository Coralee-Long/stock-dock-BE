package com.stockdock.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Document(collection = "historical_stocks") // Collection name in DB
public record HistoricalStock(
    @Id String id,
    String symbol,
    String currency,
    Instant timestamp,
    double open,
    double close,
    double high,
    double low,
    long volume
) {

}
