package com.stockdock.config;

import org.springframework.web.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

   @Bean
   public RestClient restClient() {
      // Create and configure RestClient here
      return RestClient.create();
   }
}
