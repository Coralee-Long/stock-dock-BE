package com.stockdock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "symbols")
public class SymbolConfig {
   private List<String> predefined;

   public List<String> getPredefined() {
      return predefined;
   }

   public void setPredefined(List<String> predefined) {
      this.predefined = predefined;
   }

   @PostConstruct
   public void debugSymbols() {
      System.out.println("Loaded Symbols: " + predefined);
   }
}
