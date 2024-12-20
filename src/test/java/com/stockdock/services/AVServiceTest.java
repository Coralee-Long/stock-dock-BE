package com.stockdock.services;

import com.stockdock.clients.AlphaVantageClient;
import com.stockdock.dto.GlobalQuote;
import com.stockdock.dto.GlobalQuoteResponse;
import com.stockdock.exceptions.ApiRequestException;
import com.stockdock.models.ETF;
import com.stockdock.repos.ETFRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
   void fetchAndSaveETFData_validResponse_savesETF() {
      // Arrange
      String symbol = "VOO";
      GlobalQuote quote = new GlobalQuote(symbol, "530.00", "540.00", "520.00", "538.94", "10000", "2024-12-19", "530.00", "8.94", "1.69%");
      GlobalQuoteResponse response = new GlobalQuoteResponse(quote);
      when(alphaVantageClient.getQuote(symbol)).thenReturn(response);

      ETF expectedETF = new ETF(null, symbol, "Example ETF Name", 538.94, "USD", "2024-12-19");
      when(etfRepo.save(any(ETF.class))).thenReturn(expectedETF);

      // Act
      ETF result = avService.fetchAndSaveETFData(symbol);

      // Assert
      assertNotNull(result);
      assertEquals(expectedETF.symbol(), result.symbol());
      assertEquals(expectedETF.price(), result.price());
      verify(etfRepo, times(1)).save(any(ETF.class));
      verify(alphaVantageClient, times(1)).getQuote(symbol);
   }

   @Test
   void fetchAndSaveETFData_invalidResponse_throwsException() {
      // Arrange
      String symbol = "INVALID";
      when(alphaVantageClient.getQuote(symbol)).thenReturn(null);

      // Act & Assert
      Exception exception = assertThrows(
          ApiRequestException.class,
          () -> avService.fetchAndSaveETFData(symbol));

      // Verify exception message
      assertEquals("Invalid response from Alpha Vantage for symbol: INVALID", exception.getMessage());

      // Verify that the repository save method was never called
      verify(etfRepo, never()).save(any(ETF.class));
   }
}
