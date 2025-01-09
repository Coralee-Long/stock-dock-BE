package com.stockdock.models;

import java.time.LocalDate;

public record HistoricalBar(
    LocalDate date,    // Date of the data point
    double open,       // Open price
    double close,      // Close price
    double high,       // High price
    double low,        // Low price
    long volume        // Volume traded
) {}
