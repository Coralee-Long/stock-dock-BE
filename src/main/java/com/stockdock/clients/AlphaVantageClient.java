package com.stockdock.clients;

import com.stockdock.dto.GlobalQuoteResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AlphaVantageClient {

   private final WebClient webClient;
   private final String apiKey;

   public AlphaVantageClient(WebClient webClient, String apiKey) {
      this.webClient = webClient;
      this.apiKey = apiKey;
   }

   public GlobalQuoteResponse getQuote(String symbol) {
      String url = buildAlphaVantageUrl(symbol);
      return webClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(GlobalQuoteResponse.class) // Converts response to DTO
          .block(); // Block for synchronous response
   }

   private String buildAlphaVantageUrl(String symbol) {
      return String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, apiKey);
   }
}
