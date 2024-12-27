package com.stockdock.services;

import com.stockdock.clients.AlphaVantageClient;
import com.stockdock.dto.GlobalQuote;
import com.stockdock.dto.GlobalQuoteResponse;
import com.stockdock.exceptions.ApiRequestException;
import com.stockdock.models.ETF;
import com.stockdock.repos.ETFRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AVServiceTest {

   @Mock
   private ETFRepo etfRepo;

   @Mock
   private AlphaVantageClient alphaVantageClient;

   @InjectMocks
   private AVService avService;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void fetchAndPersistETFData_validResponse_savesETF() {
      // Given: Mock API response
      String symbol = "VOO";
      GlobalQuote quote = new GlobalQuote(
          symbol,                     // 01. symbol
          "530.00",                   // 02. open
          "540.00",                   // 03. high
          "520.00",                   // 04. low
          "538.94",                   // 05. price
          "10000",                    // 06. volume
          "2024-12-19",               // 07. latest trading day
          "530.00",                   // 08. previous close
          "8.94",                     // 09. change
          "1.69%"                     // 10. change percent
      );
      GlobalQuoteResponse response = new GlobalQuoteResponse(quote);

      when(alphaVantageClient.getQuote(symbol)).thenReturn(response);

      ETF expectedETF = new ETF(
          null,
          symbol,
          "Example ETF Name",
          530.00,
          540.00,
          520.00,
          538.94,
          10000L,
          "USD",
          "2024-12-19",
          530.00,
          8.94,
          "1.69%"
      );

      when(etfRepo.save(argThat(etf ->
                                    etf.symbol().equals(expectedETF.symbol()) &&
                                    etf.price().equals(expectedETF.price())
                               ))).thenReturn(expectedETF);

      // When: Call the service method
      ETF result = avService.fetchAndPersistETFData(symbol);

      // Then: Verify the service behavior
      assertNotNull(result);
      assertEquals(expectedETF.symbol(), result.symbol());
      assertEquals(expectedETF.price(), result.price());
      verify(etfRepo, times(1)).save(any(ETF.class));
      verify(alphaVantageClient, times(1)).getQuote(symbol);
   }

   @Test
   void fetchAndPersistETFData_existingRecord_updatesETF() {
      // Given: Existing ETF and mock API response
      String symbol = "VOO";
      GlobalQuote quote = new GlobalQuote(
          symbol,
          "530.00",
          "540.00",
          "520.00",
          "538.94",
          "10000",
          "2024-12-19",
          "530.00",
          "8.94",
          "1.69%"
      );
      GlobalQuoteResponse response = new GlobalQuoteResponse(quote);
      when(alphaVantageClient.getQuote(symbol)).thenReturn(response);

      ETF existingETF = new ETF(
          "1",
          symbol,
          "Example ETF Name",
          525.00,
          535.00,
          515.00,
          520.00,
          9000L,
          "USD",
          "2024-12-18",
          530.00,
          5.00,
          "1.00%"
      );

      when(etfRepo.findBySymbol(symbol)).thenReturn(Optional.of(existingETF));
      when(etfRepo.save(any(ETF.class))).thenAnswer(invocation -> invocation.getArgument(0));

      // When: Call the service method
      ETF result = avService.fetchAndPersistETFData(symbol);

      // Then: Verify the outcome
      assertNotNull(result);
      assertEquals(538.94, result.price(), "Resulting ETF price should match updated price");
      verify(alphaVantageClient, times(1)).getQuote(symbol);
      verify(etfRepo, times(1)).save(any(ETF.class));
   }

   @Test
   void fetchAllPredefinedETFs_successfulFetch() {
      // Given: Mock API response for predefined tickers
      GlobalQuote quote = new GlobalQuote(
          "VOO",
          "530.00", "540.00", "520.00", "538.94", "10000",
          "2024-12-19", "530.00", "8.94", "1.69%"
      );
      GlobalQuoteResponse response = new GlobalQuoteResponse(quote);
      when(alphaVantageClient.getQuote(anyString())).thenReturn(response);

      ETF mockETF = new ETF(
          null,
          "VOO",
          "Example ETF Name",
          530.00, 540.00, 520.00, 538.94,
          10000L, "USD", "2024-12-19",
          530.00, 8.94, "1.69%"
      );
      when(etfRepo.save(any(ETF.class))).thenReturn(mockETF);

      // When: Call the service method
      List<ETF> result = avService.fetchAllPredefinedETFs();

      // Then: Verify the outcome
      assertEquals(10, result.size());
      verify(alphaVantageClient, times(10)).getQuote(anyString());
      verify(etfRepo, times(10)).save(any(ETF.class));
   }

   @Test
   void fetchAllPredefinedETFs_partialFailure() {
      // Given: Mock API response with partial failure
      when(alphaVantageClient.getQuote("VOO")).thenThrow(new ApiRequestException("API Error for VOO"));

      GlobalQuote spyQuote = new GlobalQuote(
          "SPY",
          "430.00", "440.00", "420.00", "438.94", "20000",
          "2024-12-19", "430.00", "8.94", "2.08%"
      );
      GlobalQuoteResponse spyResponse = new GlobalQuoteResponse(spyQuote);
      when(alphaVantageClient.getQuote("SPY")).thenReturn(spyResponse);

      ETF spyETF = new ETF(
          null, "SPY", "Example ETF Name",
          430.00, 440.00, 420.00, 438.94,
          20000L, "USD", "2024-12-19",
          430.00, 8.94, "2.08%"
      );
      when(etfRepo.save(any(ETF.class))).thenReturn(spyETF);

      // When: Call the service method
      List<ETF> result = avService.fetchAllPredefinedETFs();

      // Then: Verify the outcome
      assertEquals(1, result.size());
      assertEquals("SPY", result.get(0).symbol());
      verify(alphaVantageClient, times(1)).getQuote("VOO");
      verify(alphaVantageClient, times(1)).getQuote("SPY");
      verify(etfRepo, times(1)).save(any(ETF.class));
   }
}