package com.stockdock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record HistoricalBar(
    @JsonProperty("t") Instant timestamp, // Timestamp for the bar
    @JsonProperty("o") double open,       // Open price
    @JsonProperty("c") double close,      // Close price
    @JsonProperty("h") double high,       // High price
    @JsonProperty("l") double low,        // Low price
    @JsonProperty("v") long volume        // Trading volume
) {
}
