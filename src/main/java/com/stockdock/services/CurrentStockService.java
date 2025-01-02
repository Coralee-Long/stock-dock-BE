package com.stockdock.services;

import com.stockdock.clients.CurrentStockClient;
import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotes;
import com.stockdock.models.CurrentStock;
import com.stockdock.repos.CurrentStockRepo;
import org.springframework.stereotype.Service;

@Service
public class CurrentStockService {

   private final CurrentStockClient currentStockClient;
   private final CurrentStockRepo currentStockRepo;

   public CurrentStockService (CurrentStockClient currentStockClient, CurrentStockRepo currentStockRepo) {
      this.currentStockClient = currentStockClient;
      this.currentStockRepo = currentStockRepo;
   }

   /**
    * Fetch all quotes for the predefined list of symbols from the Alpaca API.
    * @return StockQuotes containing the latest quotes for all predefined symbols.
    */
   public StockQuotes fetchAllQuotes () {
      return currentStockClient.getAllQuotes();
   }

   /**
    * Fetch a single quote by symbol from the Alpaca API.
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    * @return StockQuote containing the latest quote for the given symbol.
    */
   public StockQuote fetchQuoteBySymbol (String symbol) {
      return currentStockClient.getSingleQuoteBySymbol(symbol);
   }

   /**
    * Fetch all quotes for predefined symbols from the Alpaca API and save them to MongoDB.
    * Replaces existing data in the 'current_stocks' collection for each symbol.
    */
   public void saveAllQuotesToDb() {
      // Get all quotes from the API
      StockQuotes response = fetchAllQuotes();

      // Map the quotes to CurrentStock objects and save to the database
      response.quotes().forEach((symbol, stockQuote) -> {
         CurrentStock currentStock = new CurrentStock(
             symbol,
             response.currency(), // Use the currency from the response (e.g., USD)
             stockQuote           // Latest quote data for the stock
         );
         currentStockRepo.save(currentStock); // Save or update the document in MongoDB
      });
   }
}

