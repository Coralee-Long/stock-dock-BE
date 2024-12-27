package com.stockdock.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler (ApiRequestException.class)
	public ResponseEntity<String> handleApiRequestException (ApiRequestException e) {
		logger.error("API request failed: {}", e.getMessage());
		return ResponseEntity.status(500).body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception e) {
		logger.error("An unexpected error occurred: {}", e.getMessage());
		return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
	}
}
