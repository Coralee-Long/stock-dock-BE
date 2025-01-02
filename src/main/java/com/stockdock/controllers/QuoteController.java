package com.stockdock.controllers;

import com.stockdock.dto.StockQuotesResponse;
import com.stockdock.services.QuoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/quotes")
public class QuoteController {

   private final QuoteService quoteService;

   public QuoteController(QuoteService quoteService) {
      this.quoteService = quoteService;
   }

   /**
    * Endpoint to fetch all predefined stock quotes.
    * @return StockQuotesResponse containing the latest quotes for all predefined symbols.
    */
   @GetMapping
   public StockQuotesResponse getAllQuotes() {
      return quoteService.fetchAllQuotes();
   }
}
