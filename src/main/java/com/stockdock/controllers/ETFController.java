package com.stockdock.controllers;

import com.stockdock.models.ETF;
import com.stockdock.services.AVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling ETF-related API requests.
 * Provides endpoints to interact with the Alpha Vantage API and manage ETF data.
 */
@RestController
public class ETFController {

	private final AVService avService;

	/**
	 * Constructor to inject the AVService dependency.
	 *
	 * @param avService Service for interacting with the Alpha Vantage API.
	 */
	@Autowired
	public ETFController(AVService avService) {
		this.avService = avService;
	}

	/**
	 * Fetches real-time data for a given ETF symbol and saves it to the database.
	 *
	 * @param symbol The stock ticker symbol (e.g., "VOO").
	 * @return The ETF data fetched and saved.
	 */
	@GetMapping(value = "/api/v1/etf", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ETF> getETFData(@RequestParam String symbol) {
		ETF etfData = avService.fetchAndSaveETFData(symbol);
		return ResponseEntity.ok(etfData);
	}
}
