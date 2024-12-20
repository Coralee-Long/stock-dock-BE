package com.stockdock.config;

import com.stockdock.clients.AlphaVantageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AlphaVantageConfig {

   @Bean
   public AlphaVantageClient alphaVantageClient(WebClient webClient, @Value("${stockdock.alpha.vantage.api-key}") String apiKey) {
      return new AlphaVantageClient(webClient, apiKey);
   }
}
