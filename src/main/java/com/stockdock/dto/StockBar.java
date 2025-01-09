package com.stockdock.dto;

import java.time.Instant;

public record StockBar(
    double c,   // Close price
    double h,   // High price
    double l,   // Low price
    int n,      // Number of trades
    double o,   // Open price
    Instant t,  // Timestamp
    int v,      // Volume
    double vw   // Volume-weighted average price
) {
}
