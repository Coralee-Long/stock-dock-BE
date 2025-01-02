package com.stockdock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "symbols")
public class SymbolConfig {
   private List<String> predefined;

   // Getter
   public List<String> getPredefined() {
      return predefined;
   }

   // Setter
   public void setPredefined(List<String> predefined) {
      this.predefined = predefined;
   }
}
