package com.stockdock.dto;

public record StockQuoteResponse(
    String symbol,      // Stock symbol
    StockQuote quote    // Quote details (mapped to StockQuote record)
) {
}