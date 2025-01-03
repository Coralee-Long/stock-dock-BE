package com.stockdock.controllers;

import com.stockdock.models.HistoricalStock;
import com.stockdock.services.HistoricalStockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/historical")
public class HistoricalStockController {

   private final HistoricalStockService historicalStockService;

   public HistoricalStockController(HistoricalStockService historicalStockService) {
      this.historicalStockService = historicalStockService;
   }

   /**
    * Fetch and save historical stock data from the Alpaca API.
    *
    * @param symbol    The stock symbol (e.g., AAPL).
    * @param startDate Start date for historical data (YYYY-MM-DD).
    * @param endDate   End date for historical data (YYYY-MM-DD).
    * @return List of HistoricalStock objects saved to the database.
    */
   @PostMapping("/fetch-and-save")
   public List<HistoricalStock> fetchAndSaveHistoricalDataForSymbol(
       @RequestParam String symbol,
       @RequestParam String startDate,
       @RequestParam String endDate
                                                          ) {
      return historicalStockService.fetchAndSaveHistoricalDataForSymbol(symbol, startDate, endDate);
   }

   /**
    * Retrieve historical stock data from the database for a specific symbol.
    *
    * @param symbol The stock symbol (e.g., AAPL).
    * @return List of HistoricalStock objects from the database.
    */
   @GetMapping("/from-db")
   public List<HistoricalStock> getHistoricalDataFromDb(@RequestParam String symbol) {
      return historicalStockService.getHistoricalDataFromDb(symbol);
   }
}
