package com.stockdock.schedulers;

import com.stockdock.services.CurrentStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class CurrentStockScheduler {

   private final static Logger logger = LoggerFactory.getLogger(CurrentStockScheduler.class);
   private final CurrentStockService currentStockService;

   public CurrentStockScheduler(CurrentStockService currentStockService) {
      this.currentStockService = currentStockService;
   }

   /**
    * Scheduled task to fetch and save current stock data for all predefined symbols.
    *
    * This task runs every 5 minutes and fetches the latest stock prices for all predefined
    * symbols from the Alpaca API. The fetched data is then saved to the MongoDB database.
    *
    * If an error occurs during the process, it logs the error and continues to the next scheduled run.
    *
    * The predefined list of stock symbols is configured in the application's symbol configuration.
    */
   @Scheduled (fixedRate = 300000) // Runs every 5 minutes (300,000 ms)
   public void scheduleCurrentStock() {
      logger.info("Scheduled task started: Fetching current stock data...");

      try {
         // Fetch and save current stock data
         currentStockService.saveAllQuotesToDb();
         logger.info("Scheduled task completed: Fetched current stock data");
      } catch (Exception e) {
         logger.error("Scheduled task failed: {}", e.getMessage(), e);
      }
   }
}
