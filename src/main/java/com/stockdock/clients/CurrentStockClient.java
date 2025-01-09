
package com.stockdock.clients;

import com.stockdock.config.SymbolConfig;
import com.stockdock.dto.HistoricalBarsResponse;
import com.stockdock.dto.StockQuoteResponse;
import com.stockdock.dto.StockQuotes;
import com.stockdock.dto.StockSnapshotResponse;
import com.stockdock.services.CurrentStockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.List;

@Service
public class CurrentStockClient {

   private final RestClient restClient;
   private final SymbolConfig symbolConfig; // Inject Symbols list
   private static final Logger logger = LoggerFactory.getLogger(CurrentStockService.class);

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
   public StockQuoteResponse getSingleQuoteBySymbol (String symbol) {
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/{symbol}/quotes/latest")
          .buildAndExpand(symbol)
          .toUri();
      System.out.println("Constructed URI: " + uri);

      // Make API Call
      return restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(StockQuoteResponse.class); // Convert response to StockQuoteResponse
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

   // Fetch detailed data for a single stock
   public StockSnapshotResponse getStockSnapshot (String symbol) {
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/{symbol}/snapshot") // Use the snapshot endpoint
          .buildAndExpand(symbol)
          .toUri();

      System.out.println("Fetching snapshot for symbol: " + symbol);

      // Make API Call
      return restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(StockSnapshotResponse.class); // Convert response to DTO
   }

   /**
    * Fetch historical stock bars from the Alpaca API.
    *
    * This method retrieves historical bar data for a specific stock symbol
    * within a given timeframe and date range. The bars include aggregated
    * data such as open, high, low, close prices, volume, and more.
    *
    * @param symbol    The stock symbol to fetch (e.g., "AAPL").
    * @param timeframe The aggregation timeframe for the bars (e.g., "1Day", "1Week", "1Month").
    *                  Accepted values should match Alpaca's API requirements.
    * @param start     The start date for the historical data in RFC-3339 format (e.g., "2024-01-01T00:00:00Z")
    *                  or simple date format (e.g., "2024-01-01").
    * @param end       The end date for the historical data in RFC-3339 format (e.g., "2024-12-31T23:59:59Z")
    *                  or simple date format (e.g., "2024-12-31").
    * @return A {@link HistoricalBarsResponse} containing the historical bars data.
    *         This includes a list of bars sorted by the provided timeframe.
    * @throws IllegalArgumentException if any required parameter is null or empty.
    */
   public HistoricalBarsResponse getHistoricalBars(String symbol, String timeframe, String start, String end) {
      URI uri = UriComponentsBuilder.fromUriString(baseUrl)
          .path("/v2/stocks/{symbol}/bars")
          .queryParam("timeframe", timeframe)
          .queryParam("start", start)
          .queryParam("end", end)
          .buildAndExpand(symbol)
          .toUri();

      logger.info("Calling Alpaca API for historical bars: {}", uri);

      return restClient.get()
          .uri(uri)
          .headers(httpHeaders -> {
             httpHeaders.set("APCA-API-KEY-ID", apiKey);
             httpHeaders.set("APCA-API-SECRET-KEY", apiSecret);
             httpHeaders.set("Accept", "application/json");
          })
          .retrieve()
          .body(HistoricalBarsResponse.class);
   }
}