package com.stockdock.clients;

import com.stockdock.config.AlphaVantageProperties;
import com.stockdock.dto.GlobalQuoteResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AlphaVantageClient {

   private final RestClient restClient;
   private final String apiKey;

   public AlphaVantageClient(RestClient restClient, AlphaVantageProperties properties) {
      this.restClient = restClient;
      this.apiKey = properties.getApiKey();
   }

   public GlobalQuoteResponse getQuote(String symbol) {
      String url = buildAlphaVantageUrl(symbol);
      return restClient.get()
          .uri(url)
          .retrieve()
          .body(GlobalQuoteResponse.class);
   }

   private String buildAlphaVantageUrl(String symbol) {
      return String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", symbol, apiKey);
   }
}
