package com.stockdock.controllers;

import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotesResponse;
import com.stockdock.services.QuoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/quotes")
public class QuoteController {

   private final QuoteService quoteService;

   public QuoteController(QuoteService quoteService) {
      this.quoteService = quoteService;
   }

   /**
    * Endpoint to fetch a single stock quote by symbol.
    * @param symbol The stock symbol to fetch (e.g., AAPL).
    * @return StockQuote containing the latest quote for the given symbol.
    */
   @GetMapping("/{symbol}")
   public StockQuote getSingleQuote(@PathVariable String symbol) {
      return quoteService.fetchQuoteBySymbol(symbol);
   }

   /**
    * Endpoint to fetch all predefined stock quotes.
    * @return StockQuotesResponse containing the latest quotes for all predefined symbols.
    */
   @GetMapping("/all")
   public StockQuotesResponse getAllQuotes() {
      return quoteService.fetchAllQuotes();
   }

   /**
    * Endpoint to fetch and save all stock quotes to MongoDB.
    * @return A confirmation message after saving.
    */
   @PostMapping("/save")
   public String saveQuotes() {
      quoteService.saveAllQuotesToDb();
      return "All quotes saved to MongoDB.";
   }
}
