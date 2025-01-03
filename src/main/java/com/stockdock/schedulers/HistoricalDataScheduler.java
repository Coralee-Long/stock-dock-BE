package com.stockdock.schedulers;

import com.stockdock.services.HistoricalStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class HistoricalDataScheduler {

   private static final Logger logger = LoggerFactory.getLogger(HistoricalDataScheduler.class);
   private final HistoricalStockService historicalStockService;

   public HistoricalDataScheduler (HistoricalStockService historicalStockService) {
      this.historicalStockService = historicalStockService;
   }

   /**
    * Scheduled task to fetch and save historical stock data for all predefined symbols.
    *
    * This task runs daily at 5 AM and fetches historical stock data for the previous day
    * from the Alpaca API. The fetched data is then saved to the MongoDB database.
    *
    * If an error occurs during the process, it logs the error and continues to the next scheduled run.
    */
   @Scheduled(cron = "0 0 5 * * *") // Runs daily at 5 AM
   public void fetchAndSaveHistoricalData () {
      LocalDate yesterday = LocalDate.now().minusDays(1);
      String startDate = yesterday.format(DateTimeFormatter.ISO_DATE);
      String endDate = startDate;

      logger.info("Scheduled job started: Fetching historical data for date {}", startDate);

      try {
         // Fetch and save historical data for all predefined symbols
         historicalStockService.fetchAndSaveHistoricalDataForAllSymbols(startDate, endDate);
         logger.info("Scheduled job completed successfully for date {}", startDate);
      }
      catch (Exception e) {
         // Catch any exceptions thrown by the service layer
         logger.error("Scheduled job failed for date {}: {}", startDate, e.getMessage());
      }
   }
}

