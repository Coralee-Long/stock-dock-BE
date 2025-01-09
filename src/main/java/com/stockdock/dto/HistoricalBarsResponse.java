package com.stockdock.dto;

import java.util.List;

public record HistoricalBarsResponse(
    List<HistoricalBar> bars
) {}