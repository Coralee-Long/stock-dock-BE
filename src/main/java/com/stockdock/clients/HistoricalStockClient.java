package com.stockdock.clients;

import com.stockdock.config.SymbolConfig;
import com.stockdock.dto.HistoricalData;
import com.stockdock.models.HistoricalStock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class HistoricalStockClient {

   private final RestClient restClient;
   private final SymbolConfig symbolConfig; // Inject Symbols list

   private final String apiKey;
   private final String apiSecret;
   private final String baseUrl;

   public HistoricalStockClient(
       SymbolConfig symbolConfig,
       @Value("${alpaca.api.key}") String apiKey,
       @Value("${alpaca.api.secret}") String apiSecret,
       @Value("${alpaca.api.base.url}") String baseUrl
                               ) {
      this.restClient = RestClient.builder().build();
      this.symbolConfig = symbolConfig;
      this.apiKey = apiKey;
      this.apiSecret = apiSecret;
      this.baseUrl = baseUrl;
   }

   // Fetch historical data for a single stock symbol
   public List<HistoricalStock> getHistoricalDataForSymbol(String symbol, String startDate, String endDate) {
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/{symbol}/bars")
          .queryParam("timeframe", "1Day") // Example: Fetch daily data
          .queryParam("start", startDate)  // Start date for historical data
          .queryParam("end", endDate)      // End date for historical data
          .buildAndExpand(symbol)
          .toUri();

      // Call the API and deserialize the response into HistoricalData
      HistoricalData response = restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(HistoricalData.class);

      // Map the response to HistoricalStock objects
      return response.bars().stream()
          .map(bar -> new HistoricalStock(
              UUID.randomUUID().toString(), // Unique ID for MongoDB
              symbol,
              "USD",                         // Currency (hardcoded for now)
              bar.timestamp(),
              bar.open(),
              bar.close(),
              bar.high(),
              bar.low(),
              bar.volume()
          ))
          .toList();
   }
}
