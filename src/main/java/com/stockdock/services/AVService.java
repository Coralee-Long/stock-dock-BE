package com.stockdock.services;

import com.stockdock.clients.AlphaVantageClient;
import com.stockdock.dto.GlobalQuote;
import com.stockdock.dto.GlobalQuoteResponse;
import com.stockdock.exceptions.ApiRequestException;
import com.stockdock.models.ETF;
import com.stockdock.repos.ETFRepo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AVService {

	private final ETFRepo etfRepo;
	private final AlphaVantageClient avClient;
	private static final Logger logger = LoggerFactory.getLogger(AVService.class);
	private static final String DEFAULT_CURRENCY = "USD";
	private static final List<String> PREDEFINED_TICKERS = List.of("VOO", "SPY", "QQQ", "DIA", "ARKK", "VTI", "EFA", "TLT", "GLD", "XLE");

	public AVService(ETFRepo etfRepo, AlphaVantageClient avClient) {
		this.etfRepo = etfRepo;
		this.avClient = avClient;
	}

	public ETF fetchAndPersistETFData(String symbol) {
		logger.info("Fetching data for symbol: {}", symbol);

		// Fetch data from the AlphaVantage client
		GlobalQuoteResponse response = avClient.getQuote(symbol);
		if (response == null || response.globalQuote() == null || response.globalQuote().symbol() == null) {
			logger.error("Invalid response from Alpha Vantage for symbol: {}", symbol);
			throw new ApiRequestException("Invalid response from Alpha Vantage for symbol: " + symbol);
		}
		logger.debug("Received response: {}", response.globalQuote());

		// Fetch existing ETF if available
		Optional<ETF> existingEtf = etfRepo.findBySymbol(symbol);
		logger.debug("Found existing ETF: {}", existingEtf);

		// Map data to ETF object
		ETF etf = mapToETF(response, existingEtf);
		logger.debug("Mapped ETF before saving: {}", etf);

		// Save ETF to repository
		ETF savedEtf = etfRepo.save(etf);
		logger.info("Successfully persisted data for symbol: {}", symbol);
		logger.debug("Persisted ETF: {}", savedEtf);

		return savedEtf;
	}

	public List<ETF> fetchAllPredefinedETFs() {
		logger.info("Fetching data for predefined ETFs...");
		List<ETF> etfs = new ArrayList<>();

		for (String ticker : PREDEFINED_TICKERS) {
			try {
				etfs.add(fetchAndPersistETFData(ticker));
			} catch (ApiRequestException e) {
				logger.error("API request error for ticker: {}", ticker, e);
			} catch (IllegalArgumentException e) {
				logger.error("Invalid argument for ticker: {}", ticker, e);
			} catch (Exception e) {
				logger.error("Unexpected error for ticker: {}", ticker, e);
			}
		}

		logger.info("Completed fetching predefined ETFs.");
		return etfs;
	}

	private ETF mapToETF(GlobalQuoteResponse response, Optional<ETF> existingEtf) {
		GlobalQuote quote = response.globalQuote();
		Long volume = parseVolume(quote.volume());
		logger.debug("Mapping to ETF: quote={}, existing ETF={}", quote, existingEtf);
		logger.debug("Existing ETF: {}", existingEtf);

		return new ETF(
			existingEtf.map(ETF::id).orElse(null), // Retain existing ID if present
			quote.symbol(), // Always use new symbol
			existingEtf.map(ETF::name).orElse("Example ETF Name"), // Retain name if present, else default
			parseDoubleOrDefault(quote.open(), 0.0),
			parseDoubleOrDefault(quote.high(), 0.0),
			parseDoubleOrDefault(quote.low(), 0.0),
			parseDoubleOrDefault(quote.price(), 0.0),
			volume,
			existingEtf.map(ETF::currency).orElse(DEFAULT_CURRENCY), // Retain currency if present
			quote.latestTradingDay(),
			parseDoubleOrDefault(quote.previousClose(), 0.0),
			parseDoubleOrDefault(quote.change(), 0.0),
			quote.changePercent()
		);
	}

	private Long parseVolume(String volume) {
		try {
			return volume != null && !volume.isEmpty() ? Long.valueOf(volume) : 0L;
		} catch (NumberFormatException e) {
			logger.warn("Invalid volume format: {}", volume);
			return 0L;
		}
	}

	private Double parseDoubleOrDefault(String value, Double defaultValue) {
		try {
			return value != null && !value.isEmpty() ? Double.valueOf(value) : defaultValue;
		} catch (NumberFormatException e) {
			logger.warn("Invalid number format: {}", value);
			return defaultValue;
		}
	}
}
