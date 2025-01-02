package com.stockdock.dto;

import java.util.Map;

public record StockQuotesResponse(
    String currency,
    Map<String, StockQuote> quotes
) {
}
