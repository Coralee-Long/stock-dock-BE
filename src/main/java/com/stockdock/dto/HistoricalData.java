package com.stockdock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record HistoricalData(
    @JsonProperty("bars") List<HistoricalBar> bars // List of historical bars
) {
}
