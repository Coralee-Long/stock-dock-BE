package com.stockdock.controllers;

import com.stockdock.dto.HistoricalBarsResponse;
import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotes;
import com.stockdock.dto.StockSnapshotResponse;
import com.stockdock.services.CurrentStockService;
import org.springframework.web.bind.annotation.*;
import com.stockdock.exceptions.InvalidSymbolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/quotes")
public class CurrentStockController {

   private final CurrentStockService currentStockService;
   private static final Logger logger = LoggerFactory.getLogger(CurrentStockService.class);

   public CurrentStockController (CurrentStockService currentStockService) {
      this.currentStockService = currentStockService;
   }

   /**
    * Endpoint to fetch a single stock quote by symbol.
    *
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    *
    * @return StockQuote containing the latest quote for the given symbol.
    */
   @GetMapping ("/{symbol}")
   public StockQuote getSingleQuote (@PathVariable String symbol) {
      return currentStockService.fetchQuoteBySymbol(symbol);
   }

   /**
    * Endpoint to fetch all predefined stock quotes.
    *
    * @return StockQuotes containing the latest quotes for all predefined symbols.
    */
   @GetMapping ("/all")
   public StockQuotes getAllQuotes () {

      return currentStockService.fetchAllQuotes();
   }

   /**
    * Endpoint to fetch detailed snapshot data for a stock.
    *
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    *
    * @return StockSnapshotResponse containing detailed stock data.
    */
   @GetMapping ("/{symbol}/snapshot")
   public StockSnapshotResponse getStockSnapshot (@PathVariable String symbol) {
      return currentStockService.fetchStockSnapshot(symbol);
   }

   /**
    * Endpoint to fetch and save all stock quotes to MongoDB.
    *
    * @return A confirmation message after saving.
    */
   @PostMapping ("/save")
   public String saveQuotes () {
      currentStockService.saveAllQuotesToDb();
      return "All quotes saved to MongoDB.";
   }

   /**
    * Endpoint to fetch historical stock bars.
    *
    * This endpoint retrieves historical bar data (e.g., open, high, low, close prices) for a specific stock
    * symbol within a given timeframe and date range. The data is aggregated based on the timeframe (e.g., "1Day", "1Week").
    *
    * @param symbol    The stock symbol to fetch historical data for (e.g., "AAPL").
    * @param timeframe The timeframe for data aggregation (e.g., "1Day", "1Week").
    *                  This should align with supported options from the frontend dropdown.
    * @param start     The start date for the historical data in RFC-3339 or YYYY-MM-DD format.
    *                  Example: "2025-01-01T00:00:00Z".
    * @param end       The end date for the historical data in RFC-3339 or YYYY-MM-DD format.
    *                  Example: "2025-01-08T00:00:00Z".
    * @return A {@link HistoricalBarsResponse} containing the historical bar data for the given symbol and timeframe.
    * @throws IllegalArgumentException if any parameter (symbol, timeframe, start, end) is null, blank, or invalid.
    * @throws InvalidSymbolException if the symbol is invalid or no historical data is found.
    */
   @GetMapping("/{symbol}/bars")
   public HistoricalBarsResponse getHistoricalBars(
       @PathVariable String symbol,
       @RequestParam(required = true) String timeframe,
       @RequestParam(required = true) String start,
       @RequestParam(required = true) String end) {
      if (symbol.isBlank() || timeframe.isBlank() || start.isBlank() || end.isBlank()) {
         throw new IllegalArgumentException("All parameters (symbol, timeframe, start, end) must be provided.");
      }

      logger.info("Fetching historical bars for symbol: {}, timeframe: {}, start: {}, end: {}", symbol, timeframe, start, end);

      return currentStockService.fetchHistoricalBars(symbol, timeframe, start, end);
   }

}