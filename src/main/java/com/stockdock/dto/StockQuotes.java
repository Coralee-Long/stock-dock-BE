package com.stockdock.dto;

import java.util.Map;

public record StockQuotes(
    String currency,
    Map<String, StockQuote> quotes
) {
}
