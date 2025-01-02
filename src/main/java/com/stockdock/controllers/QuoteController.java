//package com.stockdock.controllers;
//
//import com.stockdock.clients.AlpacaClient;
//import com.stockdock.dto.StockQuote;
//import com.stockdock.dto.StockQuotesResponse;
//import com.stockdock.services.QuoteService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/quotes")
//public class QuoteController {
//
//   private final AlpacaClient alpacaClient;
//   private final QuoteService quoteService;
//
//   public QuoteController (AlpacaClient alpacaClient, QuoteService quoteService) {
//      this.alpacaClient = alpacaClient;
//      this.quoteService = quoteService;
//   }
//
//   // Get all Quotes
//   @GetMapping
//   public StockQuotesResponse getAllQuotes () {
//      return quoteService.fetchAllQuotes();
//   }
//}
//
//
//
