package com.stockdock.models;

import org.springframework.data.annotation.Id;

/**
 * Represents an Exchange-Traded Fund (ETF) entity.
 * This record is used to store ETF details fetched from the Alpha Vantage API
 * and persisted in the MongoDB database.
 */
public record ETF(
	@Id String id,
	String symbol,
	String name,
	Double open,
	Double high,
	Double low,
	Double price,
	Long volume,
	String currency,
	String lastUpdated,
	Double previousClose,
	Double change,
	String changePercent
) {
}