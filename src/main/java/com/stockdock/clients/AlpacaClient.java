
package com.stockdock.clients;

import com.stockdock.config.SymbolConfig;
import com.stockdock.dto.StockQuotesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlpacaClient {

   private final RestClient restClient;
   private final SymbolConfig symbolConfig; // Inject Symbols list

   private final String apiKey;
   private final String apiSecret;
   private final String baseUrl;

public AlpacaClient(
    SymbolConfig symbolConfig, // Add symbols list to constructor
    @Value ("${alpaca.api.key}") String apiKey,
    @Value("${alpaca.api.secret}") String apiSecret,
    @Value("${alpaca.api.base.url}") String baseUrl
                   ) {
   this.restClient = RestClient.builder().build();
   this.symbolConfig = symbolConfig;
   this.apiKey = apiKey;
   this.apiSecret = apiSecret;
   this.baseUrl = baseUrl;
}

public StockQuotesResponse getAllQuotes() {
   // Use predefined list of symbols from Config
   List<String> symbols = symbolConfig.getPredefined();

   // Convert list of symbols into String
   String symbolsListAsQueryParam = String.join(",", symbols);

   // Build URI
   URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
       .path("/v2/stocks/{symbol}/quotes")
       .queryParam("symbols", symbolsListAsQueryParam) // Add symbols as query param
       .build()
       .toUri();

   // Make API Call
   return restClient.get()
       .uri(uri)
       .headers(httpHeaders -> {
          httpHeaders.set("APCA-API-KEY-ID", apiKey);
          httpHeaders.set("APCA-API-SECRET", apiSecret);
          httpHeaders.set("Accept", "application/json");
       })
       .retrieve()
       .body(StockQuotesResponse.class); // Convert the response to DTO
   }
}
