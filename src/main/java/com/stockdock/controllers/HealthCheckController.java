package com.stockdock.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {
   /**
    * Basic health check endpoint.
    * @return A simple message confirming the application is running.
    */
   @GetMapping
   public ResponseEntity<String> healthCheck () {
      return ResponseEntity.ok("StockDock Backend is running.");
   }
}
