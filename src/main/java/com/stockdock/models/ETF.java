package com.stockdock.models;

import org.springframework.data.annotation.Id;

/**
 * Represents an Exchange-Traded Fund (ETF) entity.
 * This record is used to store ETF details fetched from the Alpha Vantage API
 * and persisted in the MongoDB database.
 *
 * @param id         Unique identifier for the ETF document in MongoDB.
 * @param symbol     The stock ticker symbol (e.g., "VOO").
 * @param name       The name of the ETF (e.g., "Vanguard S&P 500 ETF").
 * @param price      The current price of the ETF.
 * @param currency   The currency in which the ETF is traded (e.g., "USD").
 * @param lastUpdated The date of the latest trading activity for the ETF.
 */
public record ETF(
		@Id String id,
		String symbol,
		String name,
		Double price,
		String currency,
		String lastUpdated

) {
}
