package com.stockdock.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockdock.exceptions.GlobalExceptionHandler;
import com.stockdock.models.ETF;
import com.stockdock.services.AVService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ETFControllerTest {

   @Mock
   private AVService avService;

   private MockMvc mockMvc; // Removed @Autowired

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
      ETFController etfController = new ETFController(avService);
      GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
      mockMvc = MockMvcBuilders.standaloneSetup(etfController)
          .setControllerAdvice(globalExceptionHandler) // Include GlobalExceptionHandler
          .build();
   }


   @Test
   void getSingleETF_validSymbol_returnsETF() throws Exception {
      // Given
      String symbol = "VOO";
      ETF mockETF = new ETF(
          "1", "VOO", "Example ETF Name",
          530.00, 540.00, 520.00, 538.94,
          10000L, "USD", "2024-12-19",
          530.00, 8.94, "1.69%"
      );
      when(avService.fetchAndPersistETFData(symbol)).thenReturn(mockETF);

      System.out.println("Testing with symbol: " + symbol);

      // When & Then
      mockMvc.perform(get("/api/v1/etf")
                          .param("symbol", symbol)
                          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.symbol").value("VOO"))
          .andExpect(jsonPath("$.price").value(538.94));

      verify(avService, times(1)).fetchAndPersistETFData(symbol);
   }

   @Test
   void getSingleETF_serviceThrowsException_returnsError() throws Exception {
      // Given
      String symbol = "INVALID";
      when(avService.fetchAndPersistETFData(symbol)).thenThrow(new RuntimeException("Invalid symbol"));

      // When & Then
      mockMvc.perform(get("/api/v1/etf")
                          .param("symbol", symbol)
                          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isInternalServerError())
          .andExpect(content().string("Invalid symbol: " + symbol));

      verify(avService, times(1)).fetchAndPersistETFData(symbol);
   }


   @Test
   void getAllETFs_nonEmptyList_returnsETFs() throws Exception {
      // Given
      List<ETF> mockETFs = List.of(
          new ETF(
              "1", "VOO", "Example ETF Name",
              530.00, 540.00, 520.00, 538.94,
              10000L, "USD", "2024-12-19",
              530.00, 8.94, "1.69%"
          ),
          new ETF(
              "2", "SPY", "Another ETF Name",
              430.00, 440.00, 420.00, 438.94,
              20000L, "USD", "2024-12-19",
              430.00, 8.94, "2.08%"
          )
                                  );
      when(avService.fetchAllPredefinedETFs()).thenReturn(mockETFs);

      // When & Then
      mockMvc.perform(get("/api/v1/etfs")
                          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].symbol").value("VOO"))
          .andExpect(jsonPath("$[1].symbol").value("SPY"));

      verify(avService, times(1)).fetchAllPredefinedETFs();
   }

   @Test
   void getAllETFs_emptyList_returnsNoContent() throws Exception {
      // Given
      when(avService.fetchAllPredefinedETFs()).thenReturn(Collections.emptyList());

      // When & Then
      mockMvc.perform(get("/api/v1/etfs")
                          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent());

      verify(avService, times(1)).fetchAllPredefinedETFs();
   }
}
