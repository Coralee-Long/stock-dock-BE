package com.stockdock.services;

import com.stockdock.clients.AlpacaClient;
import com.stockdock.dto.StockQuotesResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService {

   private final AlpacaClient alpacaClient;

   public QuoteService(AlpacaClient alpacaClient) {
      this.alpacaClient = alpacaClient;
   }

   /**
    * Fetch all quotes for predefined symbols.
    * @return StockQuotesResponse containing all the latest quotes.
    */
   public StockQuotesResponse fetchAllQuotes() {
      return alpacaClient.getAllQuotes();
   }

}
