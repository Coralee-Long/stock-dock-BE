package com.stockdock.services;

import com.stockdock.clients.HistoricalStockClient;
import com.stockdock.models.HistoricalStock;
import com.stockdock.repos.HistoricalStockRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalStockService {

   private final HistoricalStockClient historicalStockClient;
   private final HistoricalStockRepo historicalStockRepo;

   public HistoricalStockService(HistoricalStockClient historicalStockClient, HistoricalStockRepo historicalStockRepo) {
      this.historicalStockClient = historicalStockClient;
      this.historicalStockRepo = historicalStockRepo;
   }

   /**
    * Fetch historical stock data for a given symbol and save it to MongoDB.
    *
    * @param symbol    The stock symbol (e.g., AAPL).
    * @param startDate Start date for historical data (YYYY-MM-DD).
    * @param endDate   End date for historical data (YYYY-MM-DD).
    * @return A list of saved HistoricalStock objects.
    */
   public List<HistoricalStock> fetchAndSaveHistoricalData(String symbol, String startDate, String endDate) {
      // Fetch historical data from Alpaca API
      List<HistoricalStock> historicalData = historicalStockClient.getHistoricalDataForSymbol(symbol, startDate, endDate);

      // Save historical data to MongoDB and return saved records
      return historicalStockRepo.saveAll(historicalData);
   }

   /**
    * Retrieve historical stock data from MongoDB for a specific stock symbol.
    *
    * @param symbol The stock symbol (e.g., AAPL).
    * @return A list of HistoricalStock objects from the database.
    */
   public List<HistoricalStock> getHistoricalDataFromDb(String symbol) {
      return historicalStockRepo.findBySymbol(symbol);
   }
}
