package com.stockdock.dto;

public record StockSnapshotResponse(
    String symbol,
    StockBar dailyBar,
    StockBar prevDailyBar,
    StockQuote latestQuote,
    StockTrade latestTrade,
    StockBar minuteBar
) {
}