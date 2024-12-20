package com.stockdock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for the entire response of the "Global Quote" API from Alpha Vantage.
 */
public record GlobalQuoteResponse(
	@JsonProperty("Global Quote") GlobalQuote globalQuote
) {}