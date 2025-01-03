package com.stockdock.services;

import com.stockdock.clients.CurrentStockClient;
import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotes;
import com.stockdock.exceptions.InvalidDateRangeException;
import com.stockdock.exceptions.InvalidSymbolException;
import com.stockdock.models.CurrentStock;
import com.stockdock.repos.CurrentStockRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    *
    * @return StockQuotes containing the latest quotes for all predefined symbols.
    */
   public StockQuotes fetchAllQuotes() {
      logger.info("Fetching all stock quotes from Alpaca API.");
      StockQuotes stockQuotes = currentStockClient.getAllQuotes();

      // Defensive validation
      Objects.requireNonNull(stockQuotes, "Response from API cannot be null.");
      Objects.requireNonNull(stockQuotes.currency(), "Currency in the response cannot be null.");

      if (stockQuotes.quotes().isEmpty()) {
         throw new InvalidSymbolException("No quotes found for the predefined symbols.");
      }

      logger.info("Successfully fetched {} quotes.", stockQuotes.quotes().size());
      return stockQuotes;
   }

   /**
    * Fetch a single quote by symbol from the Alpaca API.
    *
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    * @return StockQuote containing the latest quote for the given symbol.
    */
   public StockQuote fetchQuoteBySymbol (String symbol) {
      if(symbol == null || symbol.isBlank()) {
         throw new InvalidSymbolException("Symbol cannot be null or blank.");
      }

      logger.info("Fetching stock quote for symbol {}", symbol);
      StockQuote stockQuote = currentStockClient.getSingleQuoteBySymbol(symbol);

      if (stockQuote == null) {
         throw new InvalidSymbolException("No stock quote found for symbol " + symbol);
      }

      // Defensive validation
      Objects.requireNonNull(stockQuote, "Stock quote cannot be null.");
      logger.info("Successfully fetched stock quote for symbol {}", symbol);

      return stockQuote;
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
}

