package com.stockdock.controllers;

import com.stockdock.models.ETF;
import com.stockdock.services.AVService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ETFController.class)
class ETFControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private AVService avService;

   @Test
   void testGetETFData() throws Exception {
      // Arrange
      String symbol = "VOO";
      ETF mockResponse = new ETF(
          "1",                      // id
          "VOO",                    // symbol
          "Vanguard S&P 500 ETF",   // name
          415.23,                   // price
          "USD",                    // currency
          "2024-12-20"              // lastUpdated
      );

      // Mock the AVService behavior
      when(avService.fetchAndSaveETFData(symbol)).thenReturn(mockResponse);

      // Act and Assert
      mockMvc.perform(get("/api/v1/etf")
                          .param("symbol", symbol)
                          .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("1"))
          .andExpect(jsonPath("$.symbol").value("VOO"))
          .andExpect(jsonPath("$.name").value("Vanguard S&P 500 ETF"))
          .andExpect(jsonPath("$.price").value(415.23))
          .andExpect(jsonPath("$.currency").value("USD"))
          .andExpect(jsonPath("$.lastUpdated").value("2024-12-20"));
   }
}
