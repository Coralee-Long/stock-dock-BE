package com.stockdock.dto;

import java.time.Instant;
import java.util.List;

public record StockQuote(
    double ap,         // Ask Price
    int as,            // Ask Size
    String ax,         // Ask Exchange
    double bp,         // Bid Price
    int bs,            // Bid Size
    String bx,         // Bid Exchange
    List<String> c,    // Conditions
    Instant t,         // Timestamp
    String z           // Tape
) {
}
