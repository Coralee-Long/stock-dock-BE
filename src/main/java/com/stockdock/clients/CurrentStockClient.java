
package com.stockdock.clients;

import com.stockdock.config.SymbolConfig;
import com.stockdock.dto.StockQuote;
import com.stockdock.dto.StockQuotes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CurrentStockClient {

   private final RestClient restClient;
   private final SymbolConfig symbolConfig; // Inject Symbols list

   private final String apiKey;
   private final String apiSecret;
   private final String baseUrl;
   private final String paperUrl;

   public CurrentStockClient (
       SymbolConfig symbolConfig, // Add symbols list to constructor
       @Value("${alpaca.api.key}") String apiKey,
       @Value("${alpaca.api.secret}") String apiSecret,
       @Value("${alpaca.api.base.url}") String baseUrl,
       @Value("${alpaca.api.paper.url}") String paperUrl
                             ) {
      this.restClient = RestClient.builder().build();
      this.symbolConfig = symbolConfig;
      this.apiKey = apiKey;
      this.apiSecret = apiSecret;
      this.baseUrl = baseUrl;
      this.paperUrl = paperUrl;
   }

   // Fetch single quote by symbol
   public StockQuote getSingleQuoteBySymbol (String symbol) {
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/{symbol}/snapshot") // Replace {symbol} dynamically
          .buildAndExpand(symbol)
          .toUri();

      // Make API Call
      return restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(StockQuote.class); // Convert response to a StockQuote record
   }

   // Fetch all predefined quotes
   public StockQuotes getAllQuotes () {
      // Use predefined list of symbols from Config
      List<String> symbols = symbolConfig.getPredefined();
      String symbolsListAsQueryParam = String.join(",", symbols);

      // Build URI
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/quotes/latest")
          .queryParam("symbols", symbolsListAsQueryParam) // Add symbols as query param
          .build()
          .toUri();
      System.out.println("Final URI: " + uri);

      // Make API Call
      return restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(StockQuotes.class); // Convert response to DTO
   }


}