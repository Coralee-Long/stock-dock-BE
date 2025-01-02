package com.stockdock.controllers;

import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotes;
import com.stockdock.services.CurrentStockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/quotes")
public class CurrentStockController {

   private final CurrentStockService currentStockService;

   public CurrentStockController (CurrentStockService currentStockService) {
      this.currentStockService = currentStockService;
   }

   /**
    * Endpoint to fetch a single stock quote by symbol.
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    * @return StockQuote containing the latest quote for the given symbol.
    */
   @GetMapping("/{symbol}")
   public StockQuote getSingleQuote (@PathVariable String symbol) {
      return currentStockService.fetchQuoteBySymbol(symbol);
   }

   /**
    * Endpoint to fetch all predefined stock quotes.
    * @return StockQuotes containing the latest quotes for all predefined symbols.
    */
   @GetMapping("/all")
   public StockQuotes getAllQuotes () {
      return currentStockService.fetchAllQuotes();
   }

   /**
    * Endpoint to fetch and save all stock quotes to MongoDB.
    * @return A confirmation message after saving.
    */
   @PostMapping("/save")
   public String saveQuotes() {
      currentStockService.saveAllQuotesToDb();
      return "All quotes saved to MongoDB.";
   }
}
