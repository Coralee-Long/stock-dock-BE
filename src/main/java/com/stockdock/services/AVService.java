package com.stockdock.services;

import com.stockdock.clients.AlphaVantageClient;
import com.stockdock.dto.GlobalQuoteResponse;
import com.stockdock.exceptions.ApiRequestException;
import com.stockdock.models.ETF;
import com.stockdock.repos.ETFRepo;
import org.springframework.stereotype.Service;

/**
 * Service class for handling ETF data operations.
 */
@Service
public class AVService {

	private final ETFRepo etfRepo;
	private final AlphaVantageClient avClient;

	/**
	 * Constructor for AVService.
	 *
	 * @param etfRepo           Repository for ETF data storage.
	 * @param avClient Client for interacting with Alpha Vantage API.
	 */
	public AVService(ETFRepo etfRepo, AlphaVantageClient avClient) {
		this.etfRepo = etfRepo;
		this.avClient = avClient;
	}

	/**
	 * Fetches real-time data for the specified ETF symbol from Alpha Vantage
	 * and saves it to the MongoDB database.
	 *
	 * @param symbol The ticker symbol of the ETF (e.g., "VOO").
	 * @return The saved ETF entity.
	 */
	public ETF fetchAndSaveETFData(String symbol) {
		// Fetch data from Alpha Vantage
		GlobalQuoteResponse response = avClient.getQuote(symbol);

		// Validate the response
		if (response == null || response.globalQuote() == null || response.globalQuote().symbol() == null) {
			throw new ApiRequestException("Invalid response from Alpha Vantage for symbol: " + symbol);
		}

		// Map DTO to the database entity and save it
		ETF etf = new ETF(
			null,
			response.globalQuote().symbol(),
			"Example ETF Name", // Replace with actual logic if needed
			Double.valueOf(response.globalQuote().price()),
			"USD", // Replace if currency is available in the response
			response.globalQuote().latestTradingDay()
		);

		return etfRepo.save(etf);
	}
}
