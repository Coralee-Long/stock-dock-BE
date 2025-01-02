package com.stockdock.models;

import com.stockdock.dto.StockQuote;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "current_stocks") // Collection Name
public record CurrentStock(
    @Id String symbol,       // Stock symbol (e.g., AAPL)
    String currency,         // Currency of the stock prices (e.g., USD)
    StockQuote latestQuote   // Latest quote for the stock
) {
}
