package com.stockdock.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockdock.models.ETF;
import com.stockdock.services.AVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling ETF-related API requests.
 * Provides endpoints to interact with the Alpha Vantage API and manage ETF data.
 */
@RestController
public class ETFController {

	private final AVService avService;

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
	public ResponseEntity<ETF> getSingleETF(@RequestParam String symbol) {
		ETF etfData = avService.fetchAndPersistETFData(symbol);
		return ResponseEntity.ok(etfData);
	}

	/**
	 * Fetches and persists data for a predefined list of ETFs.
	 *
	 * @return List of ETFs fetched and persisted.
	 */
	@GetMapping(value = "/api/v1/etfs", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ETF>> getAllETFs() {
		List<ETF> etfs = avService.fetchAllPredefinedETFs();
		return etfs.isEmpty()
					 ? ResponseEntity.noContent().build()
					 : ResponseEntity.ok(etfs);
	}

}
