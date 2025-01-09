package com.stockdock.dto;

import java.time.Instant;
import java.util.List;

public record StockTrade(
    List<String> c, // Conditions
    long i,         // Trade ID
    double p,       // Price
    int s,          // Size
    Instant t,      // Timestamp
    String x,       // Exchange
    String z        // Tape
) {
}