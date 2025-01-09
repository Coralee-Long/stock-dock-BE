package com.stockdock.services;

import com.stockdock.clients.CurrentStockClient;
import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuoteResponse;
import com.stockdock.dto.StockQuotes;
import com.stockdock.dto.StockSnapshotResponse;
import com.stockdock.dto.HistoricalBarsResponse;
import com.stockdock.exceptions.*;
import com.stockdock.models.CurrentStock;
import com.stockdock.repos.CurrentStockRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class CurrentStockService {

   private static final Logger logger = LoggerFactory.getLogger(CurrentStockService.class);

   private final CurrentStockClient currentStockClient;
   private final CurrentStockRepo currentStockRepo;

   public CurrentStockService (CurrentStockClient currentStockClient, CurrentStockRepo currentStockRepo) {
      this.currentStockClient = currentStockClient;
      this.currentStockRepo = currentStockRepo;
   }

   /**
    * Fetch all quotes for the predefined list of symbols from the Alpaca API.
    * If the currency in the response is null or blank, it defaults to USD.
    *
    * @return StockQuotes containing the latest quotes for all predefined symbols.
    * @throws InvalidSymbolException if no quotes are found for the predefined symbols.
    */
   public StockQuotes fetchAllQuotes() {
      logger.info("Fetching all stock quotes from Alpaca API.");

      // Fetch quotes from Alpaca API
      StockQuotes stockQuotes = currentStockClient.getAllQuotes();

      // Defensive validation
      Objects.requireNonNull(stockQuotes, "Response from API cannot be null.");

      // Default to USD if the currency is null or blank
      String currency = stockQuotes.currency();
      if (currency == null || currency.isBlank()) {
         currency = "USD";
         logger.warn("Currency in the response was null or blank. Defaulting to USD.");
      }

      if (stockQuotes.quotes().isEmpty()) {
         throw new InvalidSymbolException("No quotes found for the predefined symbols.");
      }

      logger.info("Successfully fetched {} quotes with currency {}.", stockQuotes.quotes().size(), currency);

      // Return a new StockQuotes object with the validated currency
      return new StockQuotes(currency, stockQuotes.quotes());
   }


   /**
    * Fetch a single quote by symbol from the Alpaca API.
    *
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    * @return StockQuote containing the latest quote for the given symbol.
    */
   public StockQuote fetchQuoteBySymbol(String symbol) {
      if (symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }

      logger.info("Fetching stock quote for symbol {}", symbol);

      // Fetch StockQuoteResponse
      StockQuoteResponse response = currentStockClient.getSingleQuoteBySymbol(symbol);

      if (response == null || response.quote() == null) {
         throw new InvalidSymbolException("No stock quote found for symbol " + symbol);
      }

      logger.info("Successfully fetched stock quote for symbol {}", symbol);

      // Return the StockQuote
      return response.quote();
   }

   /**
    * Fetch all quotes for predefined symbols from the Alpaca API and save them to MongoDB.
    * Replaces existing data in the 'current_stocks' collection for each symbol.
    */
   public void saveAllQuotesToDb() {
      logger.info("Fetching all stock quotes from Alpaca API to save to MongoDB.");
      // Get all quotes from the API
      StockQuotes response = fetchAllQuotes();

      logger.info("Fetched {} quotes. Saving them to MongoDB.", response.quotes().size());

      // Map the quotes to CurrentStock objects and save to the database
      response.quotes().forEach((symbol, stockQuote) -> {
         // Defensive validation
         Objects.requireNonNull(stockQuote, "Stock quote for symbol " + symbol + " cannot be null.");

         CurrentStock currentStock = new CurrentStock(
             symbol,
             response.currency(), // Use the currency from the response (e.g., USD)
             stockQuote           // Latest quote data for the stock
         );

         logger.info("Saving data for symbol: {}", symbol);
         currentStockRepo.save(currentStock); // Save or update the document in MongoDB
      });
      logger.info("All quotes have been successfully saved to MongoDB.");
   }

   /**
    * Fetches detailed snapshot data for a single stock symbol from the Alpaca API.
    *
    * @param symbol The stock symbol to fetch (e.g., "AAPL").
    * @return A {@link StockSnapshotResponse} object containing detailed data for the given symbol,
    *         including daily bar, latest trade, and latest quote information.
    * @throws InvalidSymbolException if the symbol is invalid or no snapshot data is found for the symbol.
    */
   public StockSnapshotResponse fetchStockSnapshot(String symbol) {
      if (symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }

      logger.info("Fetching snapshot for stock: {}", symbol);

      // Fetch snapshot data from Alpaca API
      StockSnapshotResponse snapshot = currentStockClient.getStockSnapshot(symbol);

      // Defensive validation
      if (snapshot == null) {
         throw new InvalidSymbolException("No snapshot data found for symbol " + symbol);
      }

      logger.info("Successfully fetched snapshot for stock: {}", symbol);
      return snapshot;
   }

   /**
    * Fetches historical stock bars for a given symbol and timeframe from the Alpaca API.
    *
    * This method retrieves aggregated historical bar data for a specific stock symbol, including
    * open, high, low, close prices, and volume for the specified timeframe and date range.
    *
    * <p>
    * The {@code timeframe} parameter specifies the aggregation period for each bar (e.g., 1Day, 1Week),
    * while the {@code start} and {@code end} parameters define the inclusive date range for the data.
    * </p>
    *
    * @param symbol    The stock symbol to fetch (e.g., "AAPL").
    * @param timeframe The timeframe for historical data aggregation (e.g., "1Day", "1Week").
    *                  Supported values align with the Alpaca API documentation.
    * @param start     The start date for the data in RFC-3339 or YYYY-MM-DD format.
    * @param end       The end date for the data in RFC-3339 or YYYY-MM-DD format.
    * @return A {@link HistoricalBarsResponse} containing the historical bars data.
    *
    * @throws InvalidSymbolException If the {@code symbol} is null, blank, or no data is found for the given symbol.
    * @throws UnsupportedTimeframeException If the {@code timeframe} is null, blank, or not supported.
    * @throws MissingRequiredParameterException If the {@code start} or {@code end} date is null or blank.
    * @throws EmptyResponseException If no historical bars are returned by the Alpaca API for the given inputs.
    */
   public HistoricalBarsResponse fetchHistoricalBars(String symbol, String timeframe, String start, String end) {
      if (symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }
      if (timeframe == null || timeframe.isBlank()) {
         throw new UnsupportedTimeframeException("Timeframe cannot be null or blank. Supported values: [1Day, 1Week, ...]");
      }
      if (start == null || start.isBlank() || end == null || end.isBlank()) {
         throw new MissingRequiredParameterException("Start and end dates cannot be null or blank.");
      }

      HistoricalBarsResponse response = currentStockClient.getHistoricalBars(symbol, timeframe, start, end);

      if (response == null || response.bars().isEmpty()) {
         throw new EmptyResponseException("No historical bars found for symbol " + symbol);
      }

      return response;
   }
}

