package com.stockdock.services;

import com.stockdock.clients.HistoricalStockClient;
import com.stockdock.config.SymbolConfig;
import com.stockdock.exceptions.InvalidDateRangeException;
import com.stockdock.exceptions.InvalidSymbolException;
import com.stockdock.models.HistoricalStock;
import com.stockdock.repos.HistoricalStockRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class HistoricalStockService {

   private static final Logger logger = LoggerFactory.getLogger(HistoricalStockService.class);

   private final HistoricalStockClient historicalStockClient;
   private final HistoricalStockRepo historicalStockRepo;
   private final SymbolConfig symbolConfig; // Inject Symbols list

   public HistoricalStockService(HistoricalStockClient historicalStockClient,
                                 HistoricalStockRepo historicalStockRepo,
                                 SymbolConfig symbolConfig) { // Include SymbolConfig in constructor
      this.historicalStockClient = historicalStockClient;
      this.historicalStockRepo = historicalStockRepo;
      this.symbolConfig = symbolConfig; // Initialize SymbolConfig
   }

   /**
    * Fetch historical stock data for a given symbol and save it to MongoDB.
    *
    * @param symbol    The stock symbol (e.g., AAPL).
    * @param startDate Start date for historical data (YYYY-MM-DD).
    * @param endDate   End date for historical data (YYYY-MM-DD).
    * @return A list of saved HistoricalStock objects.
    */
   public List<HistoricalStock> fetchAndSaveHistoricalDataForSymbol(String symbol, String startDate, String endDate) {
      // Validate input
      if (symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }

      if (!isValidDateRange(startDate, endDate)) {
         throw new InvalidDateRangeException("Invalid date range: Start date must be before end date.");
      }

      logger.info("Fetching historical data for symbol: {}, start: {}, end: {}", symbol, startDate, endDate);

      // Fetch historical data from Alpaca API
      List<HistoricalStock> historicalData = historicalStockClient.getHistoricalDataForSymbol(symbol, startDate, endDate);

      if (historicalData.isEmpty()) {
         logger.debug("No historical data returned from Alpaca API for symbol: {}, start: {}, end: {}", symbol, startDate, endDate);
         throw new InvalidSymbolException("No historical data available for the given date range and symbol: " + symbol);
      }

      logger.info("Saving {} records for symbol: {}", historicalData.size(), symbol);
      // Save historical data to MongoDB and return saved records
      return historicalStockRepo.saveAll(historicalData);
   }

   /**
    * Fetch and save historical stock data for all predefined symbols from the Alpaca API.
    *
    * This method iterates through all predefined stock symbols and fetches their historical data
    * for the specified date range. The data is then saved to the MongoDB database.
    *
    * If an error occurs while fetching data for a particular symbol, it logs the error
    * and continues with the next symbol.
    *
    * @param startDate Start date for historical data in YYYY-MM-DD format.
    * @param endDate   End date for historical data in YYYY-MM-DD format.
    */
   public void fetchAndSaveHistoricalDataForAllSymbols(String startDate, String endDate) {
      if (!isValidDateRange(startDate, endDate)) {
         throw new InvalidDateRangeException("Invalid date range: Start date must be before end date.");
      }

      List<String> symbols = symbolConfig.getPredefined(); // Fetch predefined symbols from config

      symbols.forEach(symbol -> {
         try {
            logger.info("Fetching historical data for symbol: {}", symbol);
            fetchAndSaveHistoricalDataForSymbol(symbol, startDate, endDate);
         } catch (InvalidSymbolException e) {
            logger.warn("Invalid symbol encountered: {}", symbol, e);
         } catch (InvalidDateRangeException e) {
            logger.error("Invalid date range for symbol {}: {}", symbol, e.getMessage());
         } catch (Exception e) {
            logger.error("Unexpected error while fetching data for symbol {}: {}", symbol, e.getMessage());
         }
      });
   }


   /**
    * Retrieve historical stock data from MongoDB for a specific stock symbol.
    *
    * @param symbol The stock symbol (e.g., AAPL).
    * @return A list of HistoricalStock objects from the database.
    */
   public List<HistoricalStock> getHistoricalDataFromDb(String symbol) {
      if (symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }

      logger.info("Retrieving historical data for symbol: {}", symbol);

      List<HistoricalStock> historicalData = historicalStockRepo.findBySymbol(symbol);
      if (historicalData.isEmpty()) {
         logger.debug("No historical data found in the database for symbol: {}", symbol);
         throw new InvalidDateRangeException("No historical data found for symbol: " + symbol);
      }

      return historicalData;
   }

   /**
    * Helper method to validate date ranges.
    */
   private boolean isValidDateRange(String startDate, String endDate) {
      try {
         // Parse the dates to validate format
         LocalDate start = LocalDate.parse(startDate);
         LocalDate end = LocalDate.parse(endDate);

         // Validate the startDate is before or Equal to endDate
         if (start.isAfter(end)) {
            throw new InvalidDateRangeException("Invalid date range: Start date must be before or equal to the end date.");
         }
         return true;
      } catch (DateTimeParseException e) {
         throw new InvalidDateRangeException("Invalid date format. Use YYYY-MM-DD.");
      }
   }
}
